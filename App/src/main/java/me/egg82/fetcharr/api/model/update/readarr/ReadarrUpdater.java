package me.egg82.fetcharr.api.model.update.readarr;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.egg82.arr.common.ArrType;
import me.egg82.arr.readarr.ReadarrV1API;
import me.egg82.arr.readarr.v1.Author;
import me.egg82.arr.readarr.v1.AuthorSearchCommand;
import me.egg82.arr.readarr.v1.Book;
import me.egg82.arr.readarr.v1.BookFile;
import me.egg82.arr.readarr.v1.Tag;
import me.egg82.arr.readarr.v1.schema.*;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.SelectionCancellationReason;
import me.egg82.fetcharr.api.event.update.readarr.*;
import me.egg82.fetcharr.api.model.update.AbstractUpdater;
import me.egg82.fetcharr.api.model.update.MissingStatus;
import me.egg82.fetcharr.api.model.update.UpdaterConfigImpl;
import me.egg82.fetcharr.util.WeightedRandom;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ReadarrUpdater extends AbstractUpdater {
    private final WeightedRandom<WeightedAuthor> random = new WeightedRandom<>();

    public ReadarrUpdater(@NotNull FetcharrAPI api, @NotNull ReadarrV1API arrApi, int id) {
        super(api, arrApi, new UpdaterConfigImpl(ArrType.READARR, id));
    }

    @Override
    protected boolean doWork() {
        int searchAmount = config.searchAmount();
        if (searchAmount <= 0) {
            logger.info("Skipping updating items (search amount {}) for {}_{}: {}", searchAmount, config.type().name(), config.id(), arrApi.baseUrl());
            return true;
        }

        logger.info("Updating up to {} items for {}_{}: {}", searchAmount, config.type().name(), config.id(), arrApi.baseUrl());

        Author allAuthors = arrApi.fetch(Author.class);
        if (allAuthors == null) {
            logger.error("{}_{} returned bad result for {}", config.type().name(), config.id(), Author.UNKNOWN.apiPath());
            return false; // Bad config, no need to retry every run
        }
        logger.debug("Fetched {} authors", allAuthors.resources().size());
        api.bus().post(new ReadarrFetchAuthorEvent(allAuthors, this, api));

        List<WeightedAuthor> wrapped = new ArrayList<>();
        for (AuthorResource a : allAuthors.resources()) {
            Book allBooks = arrApi.fetch(Book.class, Map.of("authorId", a.id()));
            if (allBooks == null) {
                logger.warn("{}_{} returned bad result for {}", config.type().name(), config.id(), Book.UNKNOWN.apiPath());
                continue;
            }
            logger.debug("Fetched {} albums for author {} (\"{}\")", allBooks.resources().size(), a.id(), a.authorName());
            api.bus().post(new ReadarrFetchBooksEvent(allBooks, a, this, api));
            wrapped.add(new WeightedAuthor(a, allBooks.resources()));
        }
        random.updateList(wrapped);

        boolean monitoredOnly = config.monitoredOnly();
        MissingStatus missingStatus = config.missingStatus();
        if (missingStatus == MissingStatus.ALL && config.missingOnly()) { // TODO: Temp - remove in a future version
            missingStatus = MissingStatus.MISSING;
        }
        boolean useCutoff = config.useCutoff();
        PSet<@NotNull String> skipTags = config.skipTags();

        boolean dryRun = api.updateManager().dryRun();

        List<AuthorResource> resources = new ArrayList<>();
        int attempts = 100;
        while (attempts > 0 && resources.size() < searchAmount) {
            attempts--;

            WeightedAuthor a = random.selectOne();
            if (a == null) {
                continue;
            }

            ReadarrSelectAuthorEvent selectAuthorEvent = new ReadarrSelectAuthorEvent(a.author(), this, api);
            api.bus().post(selectAuthorEvent);
            if (selectAuthorEvent.cancelled()) {
                ReadarrSkipAuthorSelectionEvent skipAuthorSelectionEvent = new ReadarrSkipAuthorSelectionEvent(a.author(), SelectionCancellationReason.PLUGIN, this, api);
                api.bus().post(skipAuthorSelectionEvent);
                if (skipAuthorSelectionEvent.cancelled()) {
                    logger.info("{} cancelled, but {} also cancelled - continuing with author {} (\"{}\")", skipAuthorSelectionEvent.getClass().getSimpleName(), skipAuthorSelectionEvent.getClass().getSimpleName(), a.author().id(), a.author().authorName());
                } else {
                    logger.info("Skipping author {} (\"{}\") due to {} cancellation", a.author().id(), a.author().authorName(), skipAuthorSelectionEvent.getClass().getSimpleName());
                    continue;
                }
            }

            if (monitoredOnly && !a.author().monitored()) {
                ReadarrSkipAuthorSelectionEvent skipAuthorSelectionEvent = new ReadarrSkipAuthorSelectionEvent(a.author(), SelectionCancellationReason.UNMONITORED, this, api);
                api.bus().post(skipAuthorSelectionEvent);
                if (skipAuthorSelectionEvent.cancelled()) {
                    logger.info("Unmonitored author {} (\"{}\"), but {} cancelled - continuing", a.author().id(), a.author().authorName(), skipAuthorSelectionEvent.getClass().getSimpleName());
                } else {
                    logger.info("Skipping author {} (\"{}\") due to unmonitored status", a.author().id(), a.author().authorName());
                    continue;
                }
            }
            if (!skipTags.isEmpty() && hasAnyTag(skipTags, a.author().tags())) {
                ReadarrSkipAuthorSelectionEvent skipAuthorSelectionEvent = new ReadarrSkipAuthorSelectionEvent(a.author(), SelectionCancellationReason.SKIP_TAG_FOUND, this, api);
                api.bus().post(skipAuthorSelectionEvent);
                if (skipAuthorSelectionEvent.cancelled()) {
                    logger.info("Author {} (\"{}\") has skip-tag set, but {} cancelled - continuing", a.author().id(), a.author().authorName(), skipAuthorSelectionEvent.getClass().getSimpleName());
                } else {
                    logger.info("Skipping author {} (\"{}\") because skip-tag is set", a.author().id(), a.author().authorName());
                    continue;
                }
            }
            if (missingStatus == MissingStatus.MISSING || missingStatus == MissingStatus.UPGRADE) {
                boolean hasFiles = true;
                for (BookResource b : a.books()) {
                    BookStatisticsResource stats = b.statistics();
                    if (stats == null || stats.bookFileCount() <= 0) {
                        hasFiles = false;
                        break;
                    }
                }
                if (missingStatus == MissingStatus.MISSING && hasFiles) {
                    ReadarrSkipAuthorSelectionEvent skipAuthorSelectionEvent = new ReadarrSkipAuthorSelectionEvent(a.author(), SelectionCancellationReason.NOT_MISSING, this, api);
                    api.bus().post(skipAuthorSelectionEvent);
                    if (skipAuthorSelectionEvent.cancelled()) {
                        logger.info("Author {} (\"{}\") not missing any book files (missing only), but {} cancelled - continuing", a.author().id(), a.author().authorName(), skipAuthorSelectionEvent.getClass().getSimpleName());
                    } else {
                        logger.info("Skipping author {} (\"{}\") because it is not missing any book files (missing only)", a.author().id(), a.author().authorName());
                        continue;
                    }
                }
                if (missingStatus == MissingStatus.UPGRADE && !hasFiles) {
                    ReadarrSkipAuthorSelectionEvent skipAuthorSelectionEvent = new ReadarrSkipAuthorSelectionEvent(a.author(), SelectionCancellationReason.MISSING, this, api);
                    api.bus().post(skipAuthorSelectionEvent);
                    if (skipAuthorSelectionEvent.cancelled()) {
                        logger.info("Author {} (\"{}\") not missing any book files (upgrade only), but {} cancelled - continuing", a.author().id(), a.author().authorName(), skipAuthorSelectionEvent.getClass().getSimpleName());
                    } else {
                        logger.info("Skipping author {} (\"{}\") because it is not missing any book files (upgrade only)", a.author().id(), a.author().authorName());
                        continue;
                    }
                }
            }
            if (useCutoff) {
                BookFile allBooks = arrApi.fetch(BookFile.class, Map.of("authorId", a.author().id()));
                if (allBooks == null) {
                    logger.warn("{}_{} returned bad result for {}", config.type().name(), config.id(), BookFile.UNKNOWN.apiPath());
                    continue;
                }
                logger.debug("Fetched {} tracks for author {} (\"{}\")", allBooks.resources().size(), a.author().id(), a.author().authorName());
                api.bus().post(new ReadarrFetchBookFilesEvent(allBooks, a.author(), this, api));

                boolean cutoffMet = true;
                for (BookFileResource b : allBooks.resources()) {
                    if (!b.qualityCutoffNotMet()) {
                        cutoffMet = false;
                        break;
                    }
                }
                if (cutoffMet) {
                    ReadarrSkipAuthorSelectionEvent skipAuthorSelectionEvent = new ReadarrSkipAuthorSelectionEvent(a.author(), SelectionCancellationReason.QUALITY_CUTOFF_MET, this, api);
                    api.bus().post(skipAuthorSelectionEvent);
                    if (skipAuthorSelectionEvent.cancelled()) {
                        logger.info("Author {} (\"{}\") quality cutoff met, but {} cancelled - continuing", a.author().id(), a.author().authorName(), skipAuthorSelectionEvent.getClass().getSimpleName());
                    } else {
                        logger.info("Skipping author {} (\"{}\") because it meets the quality cutoff", a.author().id(), a.author().authorName());
                        continue;
                    }
                }
            }

            ReadarrUpdateAuthorEvent updateAuthorEvent = new ReadarrUpdateAuthorEvent(a.author(), this, api);
            api.bus().post(updateAuthorEvent);
            if (updateAuthorEvent.cancelled()) {
                logger.info("Skipping author {} (\"{}\") due to {} cancellation", a.author().id(), a.author().authorName(), updateAuthorEvent.getClass().getSimpleName());
                continue;
            }

            if (dryRun) {
                logger.info("Would update author {} (\"{}\") if not in dry-run mode", a.author().id(), a.author().authorName());
            } else {
                logger.info("Updating author {} (\"{}\")", a.author().id(), a.author().authorName());
            }
            resources.add(a.author());
            arrApi.invalidate(Author.class, a.author().id()); // Force refresh on next
            arrApi.invalidate(Book.class, Map.of("authorId", a.author().id())); // Force refresh on next
            arrApi.invalidate(BookFile.class, Map.of("authorId", a.author().id())); // Force refresh on next
        }

        if (!dryRun && !resources.isEmpty()) {
            ReadarrPreSearchEvent preSearchEvent = new ReadarrPreSearchEvent(resources, this, api);
            api.bus().post(preSearchEvent);
            if (!preSearchEvent.cancelled()) {
                List<AuthorResource> resourcesR = new ArrayList<>();
                List<CommandResource> results = new ArrayList<>();
                for (AuthorResource r : preSearchEvent.resources()) {
                    CommandResource result = arrApi.send(new AuthorSearchCommand(r.id()), CommandResource.class);
                    if (result != null && result.id() >= 0) {
                        resourcesR.add(r);
                        results.add(result);
                    }
                }
                api.bus().post(new ReadarrPostSearchEvent(resourcesR, results, this, api));
            } else {
                logger.info("{} cancelled - not performing search for {}_{}", preSearchEvent.getClass().getSimpleName(), config.type().name(), config.id());
            }
        }

        random.clear();
        return true;
    }

    private boolean hasAnyTag(@NotNull Collection<@NotNull String> needles, @NotNull Collection<@NotNull Tag> haystack) {
        if (needles.isEmpty() || haystack.isEmpty()) {
            return false;
        }

        for (String n : needles) {
            for (Tag t : haystack) {
                TagResource r = t.resource();
                if (r != null) {
                    String label = r.label();
                    if (label != null && label.equalsIgnoreCase(n)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

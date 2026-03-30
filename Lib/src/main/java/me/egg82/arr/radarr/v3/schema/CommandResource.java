package me.egg82.arr.radarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class CommandResource extends AbstractAPIObject {
    private final int id;
    private final String name;
    private final String commandName;
    private final String message;
    private final Command body;
    private final CommandPriority priority;
    private final CommandStatus status;
    private final CommandResult result;
    private final Instant queued;
    private final Instant started;
    private final Instant ended;
    private final Duration duration;
    private final String exception;
    private final CommandTrigger trigger;
    private final String clientUserAgent;
    private final Instant stateChangeTime;
    private final boolean sendUpdatesToClient;
    private final boolean updateScheduledTask;
    private final Instant lastExecutionTime;

    public CommandResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.name = StringParser.get(obj, "name");
        this.commandName = StringParser.get(obj, "commandName");
        this.message = StringParser.get(obj, "message");
        this.body = ObjectParser.get(Command.class, api, obj, "body");
        this.priority = CommandPriority.get(CommandPriority.NORMAL, obj, "priority");
        this.status = CommandStatus.get(obj, "status");
        this.result = CommandResult.get(CommandResult.UNKNOWN, obj, "result");
        this.queued = InstantParser.get(obj, "queued");
        this.started = InstantParser.get(obj, "started");
        this.ended = InstantParser.get(obj, "ended");
        this.duration = DurationParser.get(obj, "duration");
        this.exception = StringParser.get(obj, "exception");
        this.trigger = CommandTrigger.get(CommandTrigger.UNSPECIFIED, obj, "trigger");
        this.clientUserAgent = StringParser.get(obj, "clientUserAgent");
        this.stateChangeTime = InstantParser.get(obj, "stateChangeTime");
        this.sendUpdatesToClient = BooleanParser.get(false, obj, "sendUpdatesToClient");
        this.updateScheduledTask = BooleanParser.get(false, obj, "updateScheduledTask");
        this.lastExecutionTime = InstantParser.get(obj, "lastExecutionTime");
    }

    public int id() {
        return id;
    }

    public @Nullable String name() {
        return name;
    }

    public @Nullable String commandName() {
        return commandName;
    }

    public @Nullable String message() {
        return message;
    }

    public @Nullable Command body() {
        return body;
    }

    public @NotNull CommandPriority priority() {
        return priority;
    }

    public @Nullable CommandStatus status() {
        return status;
    }

    public @NotNull CommandResult result() {
        return result;
    }

    public @Nullable Instant queued() {
        return queued;
    }

    public @Nullable Instant started() {
        return started;
    }

    public @Nullable Instant ended() {
        return ended;
    }

    public @Nullable Duration duration() {
        return duration;
    }

    public @Nullable String exception() {
        return exception;
    }

    public @NotNull CommandTrigger trigger() {
        return trigger;
    }

    public @Nullable String clientUserAgent() {
        return clientUserAgent;
    }

    public @Nullable Instant stateChangeTime() {
        return stateChangeTime;
    }

    public boolean sendUpdatesToClient() {
        return sendUpdatesToClient;
    }

    public boolean updateScheduledTask() {
        return updateScheduledTask;
    }

    public @Nullable Instant lastExecutionTime() {
        return lastExecutionTime;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CommandResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CommandResource{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", commandName='" + commandName + '\'' +
                ", message='" + message + '\'' +
                ", body=" + body +
                ", priority=" + priority +
                ", status=" + status +
                ", result=" + result +
                ", queued=" + queued +
                ", started=" + started +
                ", ended=" + ended +
                ", duration=" + duration +
                ", exception='" + exception + '\'' +
                ", trigger=" + trigger +
                ", clientUserAgent='" + clientUserAgent + '\'' +
                ", stateChangeTime=" + stateChangeTime +
                ", sendUpdatesToClient=" + sendUpdatesToClient +
                ", updateScheduledTask=" + updateScheduledTask +
                ", lastExecutionTime=" + lastExecutionTime +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}

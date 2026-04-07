package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.InstantParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;

public class Command extends AbstractAPIObject {
    private final boolean sendUpdatesToClient;
    private final Instant lastExecutionTime;
    private final Instant lastStartTime;
    private final CommandTrigger trigger;
    private final boolean suppressMessages;
    private final String clientUserAgent;

    public Command(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.sendUpdatesToClient = BooleanParser.get(false, obj, "sendUpdatesToClient");
        this.lastExecutionTime = InstantParser.get(obj, "lastExecutionTime");
        this.lastStartTime = InstantParser.get(obj, "lastStartTime");
        this.trigger = CommandTrigger.get(CommandTrigger.UNSPECIFIED, obj, "trigger");
        this.suppressMessages = BooleanParser.get(false, obj, "suppressMessages");
        this.clientUserAgent = StringParser.get(obj, "clientUserAgent");
    }

    public boolean sendUpdatesToClient() {
        return sendUpdatesToClient;
    }

    public @Nullable Instant lastExecutionTime() {
        return lastExecutionTime;
    }

    public @Nullable Instant lastStartTime() {
        return lastStartTime;
    }

    public @NotNull CommandTrigger trigger() {
        return trigger;
    }

    public boolean suppressMessages() {
        return suppressMessages;
    }

    public @Nullable String clientUserAgent() {
        return clientUserAgent;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Command command)) return false;
        return sendUpdatesToClient == command.sendUpdatesToClient && suppressMessages == command.suppressMessages && Objects.equals(lastExecutionTime, command.lastExecutionTime) && Objects.equals(lastStartTime, command.lastStartTime) && trigger == command.trigger && Objects.equals(clientUserAgent, command.clientUserAgent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sendUpdatesToClient, lastExecutionTime, lastStartTime, trigger, suppressMessages, clientUserAgent);
    }

    @Override
    public String toString() {
        return "Command{" +
                "sendUpdatesToClient=" + sendUpdatesToClient +
                ", lastExecutionTime=" + lastExecutionTime +
                ", lastStartTime=" + lastStartTime +
                ", trigger=" + trigger +
                ", suppressMessages=" + suppressMessages +
                ", clientUserAgent='" + clientUserAgent + '\'' +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}

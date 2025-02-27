package UCNDiscordBot.Listeners.ReactionListener;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

// Class to give rank on react
public class ReactionListener extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        // This runs if anyone reacts to a message

        // The eventhandler
        if (event.getUser().isBot())
            // checks if the owner of the message is a bot
            return;
        if (isChannel(event, "roles")) {
            // This is true if the reaction happened in the channel called role

            // Assign role to user
            assignRole(event);
        }
    }

    private void assignRole(MessageReactionAddEvent event) {
        List<Role> memberRoles = event.getGuild().getMemberById(event.getUserId()).getRoles();
        // Check if the reaction is the checkmark
        if (event.getReaction().getEmoji().asUnicode().equals(Emoji.fromUnicode("U+2705"))) {
            // Get message id
            String messageID = event.getMessageId();
            // Get message text using message id
            String message = event.getChannel().retrieveMessageById(messageID).complete()
                    .getContentDisplay();
            // Loop over all roles
            for (int i = 1; i < event.getGuild().getRoles().size(); i++) {
                // Get role id
                String roleID = event.getGuild().getRoles().get(i).getId();

                // Get the role name
                String roleName = event.getGuild().getRoles().get(i).getName();

                // Get role
                Role thisRole;
                thisRole = event.getGuild().getRoleById(roleID);

                // If the message content equals the role name then add the role to the user
                if (message.equals(roleName)) {
                    if (memberRoles.contains(thisRole)) {
                        System.out.println("removing role");
                        removeRole(roleID, event);
                    } else {
                        System.out.println("setting role");
                        setRole(roleID, event);
                    }

                }
            }
        }
        // Reset reactions
        resetReactions(event);
    }

    private boolean isChannel(MessageReactionAddEvent event, String channel) {
        // Checks if the message was written in a specific channel
        // this will be used is the eventhandler
        return event.getChannel().getName().equals(channel);
    }

    private void setRole(String roleId, MessageReactionAddEvent event) {
        event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(roleId)).queue();
    }

    private void removeRole(String roleId, MessageReactionAddEvent event) {
        event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById(roleId)).queue();
    }

    private void resetReactions(MessageReactionAddEvent event) {
        event.getGuildChannel().clearReactionsById(event.getMessageId()).queue();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        event.getGuildChannel().addReactionById(event.getMessageId(), Emoji.fromUnicode("U+2705")).queue();
    }
}

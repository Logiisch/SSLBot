package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import util.SteamConnector;

public class cmdLink extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommandInteraction sce = event.getInteraction();
        if (!sce.getName().equalsIgnoreCase("link")) return;

        OptionMapping steamIDom = sce.getOption("steamid");

        String teststeamid = SteamConnector.getSteamID(event.getUser().getId());
        if (teststeamid.length()>0) {
            event.reply("Error: Your Account is already linked. If you want to link to a different account, please `/unlink` first!").setEphemeral(true).queue();
            return;
        }

        if(steamIDom==null) {
            String out = "To link this discord account to your steam account, please follow these steps:\n"
                    +"Put the following text (including brackets) into the description of your steam account and hit save:\n"
                    +"`"+getVerifyCode(event.getUser().getId())+"`\n"
                    +"Then look for your steam id. A guide for getting your steam id can be found here:\n"
                    +"<https://www.thegamer.com/how-to-find-your-steam-id/>\n"
                    +"If you have your steam id, come back to discord and use the /link command again, but this time, pass your steam id as argument\n"
                    +"example: `/link 76561198315721049`";
            sce.reply(out).setEphemeral(true).queue();
            return;
        }

        String steamid = steamIDom.getAsString().trim();
        if (steamid.length()!=17) {
            sce.reply("Error: Your steam ID should have exactly 17 digits. Please check the provided id!\nIf you need help, please run `/link`!").setEphemeral(true).queue();
            return;
        }
        for (char c:steamid.toCharArray()) {
            if (!Character.isDigit(c)) {
                sce.reply("Error: Your steam ID should have exactly 17 digits. Please check the provided id!\nIf you need help, please run `/link`!").setEphemeral(true).queue();
                return;
            }
        }
        String description;

        try {
            description = SteamConnector.getDescription(steamid);
        } catch (Exception e) {
            event.reply("Error: Your steam ID is not valid, or your profile is not set to public!\nYou can only link public profiles. After linking, you can switch back to private!").setEphemeral(true).queue();
            return;
        }

        if (!description.contains(getVerifyCode(event.getUser().getId()))) {
            event.reply("Error: Your account description doesnt seem to contain the code. Please use the exact code, with brackets:\n"
                    +"`"+getVerifyCode(event.getUser().getId())+"`").setEphemeral(true).queue();
            return;
        }

        SteamConnector.addLinking(sce.getUser().getId(),steamid);
        event.reply("Success: Your Accounts were successfully linked!\nYou may now remove the code from your description.").setEphemeral(true).queue();

    }


    private static String getVerifyCode(String discordID) {
        return "[LINK:"+discordID+"]";
    }
}

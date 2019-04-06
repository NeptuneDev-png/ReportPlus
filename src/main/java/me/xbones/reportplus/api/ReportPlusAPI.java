package me.xbones.reportplus.api;

import me.xbones.reportplus.core.Core;
import me.xbones.reportplus.core.RPlayer;
import me.xbones.reportplus.core.Report;
import me.xbones.reportplus.core.configuration.ConfigurationManager;
import me.xbones.reportplus.core.exception.ExceptionType;
import me.xbones.reportplus.core.exception.ReportPlusException;
import me.xbones.reportplus.core.punishments.Punishment;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.util.List;

public class ReportPlusAPI {
    private Core main;


    public ReportPlusAPI(Core plugin){
        this.main = plugin;
    }

    public  void reportToDiscord(RPlayer reporter, String reported, String content){
        main.reportToDiscord(reporter,reported,content);
    }

    public  void reportToMinecraft(RPlayer reporter, String reported, String content){
        main.reportToStaff(reporter,reported,content);
    }

    public  void reportToBoth(RPlayer reporter, String reported, String content){
        main.reportToBoth(reporter,reported,content);
    }

    public List<Report> getReports(){
        return main.getReportPlus().getReports();
    }

    /*
    public void setReportClickInventory(Inventory customInv){
        main.getInventoryManager().setCustomCloseReportInventory(customInv);
    }
*/

    public  boolean sendPunishment(Punishment punishment){
        if((boolean) ConfigurationManager.get("Enabled-Modules.Punishment-Broadcast.Enabled")) {
            if(((String)ConfigurationManager.get("Enabled-Modules.Punishment-Broadcast.Channel-ID")).equalsIgnoreCase("[INSERT ID HERE]")){
                throw new ReportPlusException(
                        "Punishment broadcasting is not configured.", ExceptionType.NOT_CONFIGURED);

            }
            sendMessageToChannel((String)ConfigurationManager.get("Enabled-Modules.Punishment-Broadcast.Channel-ID"), new EmbedBuilder()
                    .setTitle("New Punishment")
                    .setDescription("You have received a new punishment!")
                    .setColor(Color.RED)
                    .addField("Punisher", punishment.getPunisher(), false)
                    .addField("Punished", punishment.getPunished(), false)
                    .addField("Type", punishment.getType().toString(), false));
            return true;
        }else{
            throw new ReportPlusException(
                    "Punishment broadcasting is disabled.", ExceptionType.DISABLED);
        }
    }

    public  void sendMessageToChannel(String channelID, String message){
        main.getJda().getTextChannelById(channelID).sendMessage(message).queue();
    }

    public void sendMessageToChannel(String channelID, EmbedBuilder embed){
        try {

            main.getJda().getTextChannelById(channelID).sendMessage(embed.build()).queue();
        } catch(Exception ex){

            throw new ReportPlusException(
                    "Channel not found.", ExceptionType.CHANNEL_DOES_NOT_EXIST);
        }
    }
}

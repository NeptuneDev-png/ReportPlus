package me.xbones.reportplus.core.universalcommands;

import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.api.CommandSender;
import com.github.fernthedev.fernapi.universal.api.IFPlayer;
import com.github.fernthedev.fernapi.universal.api.UniversalCommand;
import com.github.fernthedev.fernapi.universal.data.chat.ChatColor;
import com.github.fernthedev.fernapi.universal.data.chat.TextMessage;
import me.xbones.reportplus.core.IReportPlus;
import me.xbones.reportplus.core.RPlayer;
import me.xbones.reportplus.core.gson.LangConfig;

import java.util.HashMap;

public class ReportCommand extends UniversalCommand {

    private HashMap<String, Long> cooldowns = new HashMap<>();
    private IReportPlus main;
    public ReportCommand(IReportPlus main){
        super("report");
        this.main=main;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        LangConfig lang = main.getLangConfig().getConfigData();
        if (commandSender instanceof IFPlayer<?>) {
            IFPlayer<?> p = (IFPlayer<?>) commandSender;
            if (main.getBooleanFromConfig("Enabled-Modules.Reporting")) {
                if (p.hasPermission("reportplus.use")) {

                    // COOLDOWN //
                    int cooldownTime = main.getIntFromConfig("Command-cooldown"); // Get number of seconds from wherever you want
                    if(cooldowns.containsKey(p.getName())) {
                        long secondsLeft = ((cooldowns.get(p.getName())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
                        if(secondsLeft>0) {
                            // Still cooling down
                            p.sendMessage(new TextMessage(translate( main.getPrefix() + lang.getCoolDownText().replace("%secondsLeft%", secondsLeft + ""))));
                            return;
                        }
                    }
                    // No cooldown found or cooldown has expired, save new cooldown
                    cooldowns.put(p.getName(), System.currentTimeMillis());

                    // COOLDOWN END //

                    if(args.length < 2) {
                            p.sendMessage(new TextMessage(translate( main.getPrefix() + " " + main.getStringFromMessages("Not-Enough-Args"))));

                    } else {

                        IFPlayer target = Universal.getMethods().getPlayerFromName(args[0]);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < args.length; i++){
                            sb.append(args[i]).append(" ");
                        }

                        String Message = sb.toString().trim();

                        if(target == null)
                            p.sendMessage(new TextMessage(translate( main.getPrefix() + lang.getPlayerCannotBeFound())));
                        else {
                            if(target == p && !p.hasPermission("reportplus.reportSelf"))
                            {
                                p.sendMessage(new TextMessage(translate( main.getPrefix() + " " + main.getStringFromMessages("Cant-Report-Self"))));
                                return;
                            }
                            main.getCore().reportToBoth(new RPlayer(main.getCore(), p.getName(),p.getUuid()), target.getName(), Message);
                        }
                    }

                } else {
                    main.NoPerm(p);
                }
            } else {
                p.sendMessage(new TextMessage(translate( main.getPrefix() + " " + lang.getReportingIsDisabled())));
            }
        } else {
            commandSender.sendMessage(new TextMessage(translate(lang.getCanOnlyRunIngame())));
        }
    }

    private String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}

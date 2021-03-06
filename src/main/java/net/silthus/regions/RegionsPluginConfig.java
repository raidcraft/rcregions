package net.silthus.regions;

import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.annotation.ConfigurationElement;
import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration;
import de.exlll.configlib.format.FieldNameFormatters;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RegionsPluginConfig extends BukkitYamlConfiguration {

    @Comment("The relative path or config file where your skill region groups are located.")
    private String regionGroupsConfig = "groups.yml";
    private String limitsConfig = "limits.yml";
    @Comment("The location where schematics of regions should be stored.")
    private String schematics = "schematics";
    @Comment("The time in ticks how long a player has to confirm the buying of a region.")
    private long buyTimeTicks = 600L;
    @Comment({
            "Set to true to automatically set the parent WorldGuard region defined in the group config.",
            "You can always manually update the parent regions with the command /rcra wgparents <group>"
    })
    private boolean autosetWorldGuardParent = true;
    @Comment({
            "Set to true to automatically map groups based on existing WorldGuard parents.",
            "You can always manually trigger this with the command /rcra autogroup <group>"
    })
    private boolean autoMapParent = true;
    @Comment("Set this to true to display a message to players that have open sales when they login.")
    private boolean displaySalesLoginNotification = true;
    @Comment("Time in ticks the login message for open sales should be delayed.")
    private long salesLoginDelay = 60L;
    @Comment({
            "Time in minutes after which a region sale should end automatically.",
            "Set to less than 0 to disable the timeout and never expire sales."
    })
    private long regionSellTimeout = 20160;
    @Comment("Interval in ticks for the regular tasks that checks for expired sales.")
    private long expirationTaskInterval = 1200;
    private List<String> ignoredRegions = new ArrayList<>();
    @Comment({
            "A list of commands to execute as server when a region is sold.",
            "You can use the %player%, %region%, %wgregion% and %group% replacements."
    })
    private List<String> sellCommands = new ArrayList<>();
    private DatabaseConfig database = new DatabaseConfig();
    private boolean enableSchematics = false;

    public RegionsPluginConfig(Path path) {

        super(path, BukkitYamlProperties.builder().setFormatter(FieldNameFormatters.LOWER_UNDERSCORE).build());
    }

    @ConfigurationElement
    @Getter
    @Setter
    public static class DatabaseConfig {

        private String username = "sa";
        private String password = "sa";
        private String driver = "h2";
        private String url = "jdbc:h2:~/skills.db";
    }
}

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

    @Comment("The relative path where your skill region groups are located.")
    private String regionGroupsPath = "region-groups";
    private String limitsConfig = "limits.yml";
    private List<String> ignoredRegions = new ArrayList<>();
    private DatabaseConfig database = new DatabaseConfig();

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

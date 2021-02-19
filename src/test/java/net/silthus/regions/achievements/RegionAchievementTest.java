package net.silthus.regions.achievements;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import de.raidcraft.achievements.AchievementContext;
import net.silthus.regions.RegionsPlugin;
import net.silthus.regions.entities.Region;
import net.silthus.regions.entities.RegionGroup;
import net.silthus.regions.entities.RegionPlayer;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.Mockito.*;

class RegionAchievementTest {

    private ServerMock server;
    private RegionsPlugin plugin;

    private RegionAchievement achievement;
    private AchievementContext context;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(RegionsPlugin.class);
        context = mock(AchievementContext.class);
        achievement = new RegionAchievement(context);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("should give region achievement if count is met")
    void shouldGiveAchievemtnForBareCount() {

        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("count", 2);

        achievement.load(cfg);

        RegionPlayer player = RegionPlayer.getOrCreate(server.addPlayer());
        new Region("test").owner(player).save();
        new Region("test2").owner(player).save();
        player.refresh();

        achievement.check(player);

        verify(context, times(1)).addTo(any());
    }

    @Test
    @DisplayName("should not give achievement if count is not met")
    void shouldNotGiveIfCountIsNotMet() {

        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("count", 2);

        achievement.load(cfg);

        RegionPlayer player = RegionPlayer.getOrCreate(server.addPlayer());
        new Region("test").owner(player).save();
        player.refresh();

        achievement.check(player);

        verify(context, never()).addTo(any());
    }

    @Test
    @DisplayName("should only give achievement if region groups match")
    void shouldOnlyGiveAchievementIfRegionGroupsMatch() {

        new RegionGroup().identifier("test").save();
        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("count", 1);
        cfg.set("groups", Arrays.asList("test"));

        achievement.load(cfg);

        RegionPlayer player = RegionPlayer.getOrCreate(server.addPlayer());
        new Region("test").owner(player).save();
        player.refresh();

        achievement.check(player);

        verify(context, never()).addTo(any());
    }
}
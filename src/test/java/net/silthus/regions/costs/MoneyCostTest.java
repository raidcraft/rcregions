package net.silthus.regions.costs;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.SneakyThrows;
import net.milkbowl.vault.economy.Economy;
import net.silthus.regions.RegionsPlugin;
import net.silthus.regions.entities.Region;
import net.silthus.regions.entities.RegionGroup;
import net.silthus.regions.entities.RegionPlayer;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MoneyCostTest {

    private ServerMock server;
    private Economy economy;
    private RegionsPlugin plugin;
    private MoneyCost cost;

    @BeforeEach
    void setUp() {

        server = MockBukkit.mock();
        plugin = MockBukkit.load(RegionsPlugin.class);
        economy = mock(Economy.class);
        cost = new MoneyCost(economy);
    }

    @AfterEach
    void tearDown() {

        MockBukkit.unmock();
    }

    @Nested
    @DisplayName("calculate(...)")
    class calculate {

        private RegionPlayer player;
        private Region region;
        private RegionGroup group;
        private ProtectedRegion regionMock;

        @BeforeEach
        void setUp() {

            regionMock = mock(ProtectedRegion.class);
            player = RegionPlayer.getOrCreate(server.addPlayer());
            region = new Region("test");
            region.volume(100);
            region.size(10);
            region.price(0.0);
            group = RegionGroup.getOrCreate("test");
            region.group(group);
        }

        @Test
        @DisplayName("should have correct m2 size")
        void volumeSize() {

            assertThat(region.size()).isEqualTo(10);
        }

        @Nested
        @DisplayName("with STATIC cost type")
        class staticType {

            @BeforeEach
            void setUp() {

                cost.type(MoneyCost.Type.STATIC);
            }

            @SneakyThrows
            @Test
            @DisplayName("should use static price by default")
            void shouldUseStaticPriceByDefault() {

                region.price(100.0);
                assertThat(cost.calculate(region, player).total()).isEqualTo(100.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should not factor in individual cost factor of region")
            void shouldFactorInIdividualCostFactorOfRegion() {

                region.price(100.0);
                region.priceMultiplier(2.0);

                assertThat(cost.calculate(region, player).total()).isEqualTo(100.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should multiply region count with configured factor")
            void shouldMultiplyRegionCountWithFactor() {

                region.priceType(Region.PriceType.DYNAMIC);
                cost.basePrice(100.0);
                cost.regionCountMultiplier(2.0);
                player.regions().add(new Region("bar"));
                player.regions().add(new Region("foo"));

                assertThat(cost.calculate(region, player).total()).isEqualTo(400.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should not multiply base price with a region count of zero")
            void shouldNoMultiplyBasePriceIfRegionCountIsZero() {

                region.priceType(Region.PriceType.DYNAMIC);
                cost.basePrice(100.0);
                cost.regionCountMultiplier(2.0);

                assertThat(cost.calculate(region, player).total()).isEqualTo(100.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should multiply price with number of region groups")
            void shouldMultiplyBasePriceWithNumberOfGroups() {

                Region test = new Region("test");
                test.group(RegionGroup.getOrCreate("foobar"));
                player.regions().add(test);

                region.priceType(Region.PriceType.DYNAMIC);
                cost.basePrice(100.0);
                cost.regionGroupCountMultiplier(2.0);

                assertThat(cost.calculate(region, player).total()).isEqualTo(200.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should multiply price with number of regions in the same group")
            void shouldMultiplyPriceWithTheNumberOfRegionsInTheSameGroup() {

                player.regions().add(new Region("test1").group(group));
                player.regions().add(new Region("test2").group(group));

                region.priceType(Region.PriceType.DYNAMIC);
                cost.basePrice(100.0);
                cost.sameGroupCountMultiplier(2.0);

                assertThat(cost.calculate(region, player).total()).isEqualTo(400.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should return 0 if region is free")
            void shouldReturnZeroIfRegionIsFree() {

                region.priceType(Region.PriceType.FREE);

                assertThat(cost.calculate(region, player).total()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("with m3 calculation")
        class m3Type {

            @BeforeEach
            void setUp() {

                cost.type(MoneyCost.Type.PER3M);
            }

            @SneakyThrows
            @Test
            @DisplayName("should use static price by default")
            void shouldUseStaticPriceByDefault() {

                region.price(100.0);
                assertThat(cost.calculate(region, player).total()).isEqualTo(100.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should factor in individual cost factor of region")
            void shouldFactorInIdividualCostFactorOfRegion() {

                region.priceType(Region.PriceType.DYNAMIC);
                region.priceMultiplier(2.0);
                cost.basePrice(100.0);
                cost.regionCountMultiplier(2.0);
                player.regions().add(new Region("bar"));
                player.regions().add(new Region("foo"));

                assertThat(cost.calculate(region, player).total()).isEqualTo(80000.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should multiply region count with configured factor")
            void shouldMultiplyRegionCountWithFactor() {

                region.priceType(Region.PriceType.DYNAMIC);
                cost.basePrice(100.0);
                cost.regionCountMultiplier(2.0);
                player.regions().add(new Region("bar"));
                player.regions().add(new Region("foo"));

                // basePrice + (regionCount * multiplier * basePrice)
                assertThat(cost.calculate(region, player).total()).isEqualTo(40000.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should not multiply base price with a region count of zero")
            void shouldNoMultiplyBasePriceIfRegionCountIsZero() {

                region.priceType(Region.PriceType.DYNAMIC);
                cost.basePrice(100.0);
                cost.regionCountMultiplier(2.0);

                assertThat(cost.calculate(region, player).total()).isEqualTo(10000.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should multiply price with number of region groups")
            void shouldMultiplyBasePriceWithNumberOfGroups() {

                Region test = new Region("test");
                test.group(RegionGroup.getOrCreate("foobar"));
                player.regions().add(test);

                region.priceType(Region.PriceType.DYNAMIC);
                cost.basePrice(100.0);
                cost.regionGroupCountMultiplier(2.0);

                assertThat(cost.calculate(region, player).total()).isEqualTo(20000.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should multiply price with number of regions in the same group")
            void shouldMultiplyPriceWithTheNumberOfRegionsInTheSameGroup() {

                player.regions().add(new Region("test1").group(group));
                player.regions().add(new Region("test2").group(group));

                region.priceType(Region.PriceType.DYNAMIC);
                cost.basePrice(100.0);
                cost.sameGroupCountMultiplier(2.0);

                assertThat(cost.calculate(region, player).total()).isEqualTo(40000.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should return 0 if region is free")
            void shouldReturnZeroIfRegionIsFree() {

                region.priceType(Region.PriceType.FREE);

                assertThat(cost.calculate(region, player).total()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("with m2 calculation")
        class m2Type {

            @BeforeEach
            void setUp() {

                cost.type(MoneyCost.Type.PER2M);
            }

            @SneakyThrows
            @Test
            @DisplayName("should use static price by default")
            void shouldUseStaticPriceByDefault() {

                region.price(100.0);
                assertThat(cost.calculate(region, player).total()).isEqualTo(100.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should multiply region count with configured factor")
            void shouldMultiplyRegionCountWithFactor() {

                region.priceType(Region.PriceType.DYNAMIC);
                cost.basePrice(100.0);
                cost.regionCountMultiplier(2.0);
                player.regions().add(new Region("bar"));
                player.regions().add(new Region("foo"));

                // basePrice + (regionCount * multiplier * basePrice)
                assertThat(cost.calculate(region, player).total()).isEqualTo(4000.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should factor in individual cost factor of region")
            void shouldFactorInIdividualCostFactorOfRegion() {

                region.priceType(Region.PriceType.DYNAMIC);
                region.priceMultiplier(2.0);
                cost.basePrice(100.0);
                cost.regionCountMultiplier(2.0);
                player.regions().add(new Region("bar"));
                player.regions().add(new Region("foo"));

                assertThat(cost.calculate(region, player).total()).isEqualTo(8000.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should not multiply base price with a region count of zero")
            void shouldNoMultiplyBasePriceIfRegionCountIsZero() {

                region.priceType(Region.PriceType.DYNAMIC);
                cost.basePrice(100.0);
                cost.regionCountMultiplier(2.0);

                assertThat(cost.calculate(region, player).total()).isEqualTo(1000.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should multiply price with number of region groups")
            void shouldMultiplyBasePriceWithNumberOfGroups() {

                Region test = new Region("test");
                test.group(RegionGroup.getOrCreate("foobar"));
                player.regions().add(test);

                region.priceType(Region.PriceType.DYNAMIC);
                cost.basePrice(100.0);
                cost.regionGroupCountMultiplier(2.0);

                assertThat(cost.calculate(region, player).total()).isEqualTo(2000.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should multiply price with number of regions in the same group")
            void shouldMultiplyPriceWithTheNumberOfRegionsInTheSameGroup() {

                player.regions().add(new Region("test1").group(group));
                player.regions().add(new Region("test2").group(group));

                region.priceType(Region.PriceType.DYNAMIC);
                cost.basePrice(100.0);
                cost.sameGroupCountMultiplier(2.0);

                assertThat(cost.calculate(region, player).total()).isEqualTo(4000.0);
            }

            @SneakyThrows
            @Test
            @DisplayName("should return 0 if region is free")
            void shouldReturnZeroIfRegionIsFree() {

                region.priceType(Region.PriceType.FREE);

                assertThat(cost.calculate(region, player).total()).isEqualTo(0);
            }
        }
    }
}
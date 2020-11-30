package net.silthus.regions;

import lombok.Value;
import lombok.experimental.Accessors;
import net.silthus.regions.entities.Region;
import net.silthus.regions.entities.RegionPlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Supplier;

/**
 * A cost is a value that can be applied to regions and must
 * be payed when the region is bought.
 * <p>The primary use case for costs is money, but there may be
 * other costs like physical items, skill points, etc.
 * <p>Create your own cost types by implementing this interface
 * and tagging it with the @{@link CostType} annotation.
 * Every cost requires the annotation to be registered.
 * <p>No registration is needed if your cost type has a parameterless public constructor.
 * Use the {@link RegionManager#register(Class, Supplier)} if that is not the case.<br>
 * <p><pre>{@code
 * @CostType("money")
 * public class MoneyCost implements Cost {
 *     ...
 * }
 * }</pre>
 */
public interface Cost {

    /**
     * The load method is called after creating a new instance
     * of this cost class.
     * <p>Use it to load values from the configuration that are used
     * to calculate your cost.
     *
     * @param config the config section that contains the data for this cost
     */
    void load(ConfigurationSection config);

    /**
     * Calculates the cost display (description) for the given player.
     * <p>Use this method to retrieve the individual costs for each player.
     *
     * @param region the region to calculate and display the cost for
     * @param player the player to display the cost for
     * @return an individual cost display for the given player
     */
    String display(Region region, RegionPlayer player);

    /**
     * Checks if the player satisfies this cost.
     * <p>Use it to check and display the cost requirement.
     *
     * @param region the region to check the cost for
     * @param player the player to check the cost requirement for
     * @return the test result
     */
    Result check(Region region, RegionPlayer player);

    /**
     * Applies this cost to the given player removing all
     * values that were checked with {@link #check(Region, RegionPlayer)}.
     *
     * @param region the region to apply this cost for
     * @param player the player to apply this cost to
     * @return the apply result
     */
    Result apply(Region region, RegionPlayer player);

    @Value
    @Accessors(fluent = true)
    class Result {
        boolean success;
        String error;
        double price;
        ResultStatus status;

        public Result(boolean success, String error) {
            this.success = success;
            this.error = error;
            this.price = 0;
            status = ResultStatus.UNKNOWN;
        }

        public Result(boolean success, String error, ResultStatus status) {

            this.success = success;
            this.error = error;
            this.price = 0;
            this.status = status;
        }

        public Result(boolean success, String error, double price) {
            this.success = success;
            this.error = error;
            this.price = price;
            status = ResultStatus.UNKNOWN;
        }

        public Result(boolean success, String error, double price, ResultStatus status) {

            this.success = success;
            this.error = error;
            this.price = price;
            this.status = status;
        }

        public boolean failure() {
            return !success();
        }
    }

    @Value
    @Accessors(fluent = true)
    class Registration<TCost extends Cost> {
        String identifier;
        Class<TCost> costClass;
        Supplier<TCost> supplier;
    }

    enum ResultStatus {
        UNKNOWN,
        OWNED_BY_OTHER,
        OWNED_BY_SELF,
        SUCCESS,
        LIMITS_REACHED,
        NOT_ENOUGH_MONEY,
        COSTS_NOT_MET,
        OTHER;
    }
}

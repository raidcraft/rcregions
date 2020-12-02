package net.silthus.regions.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.silthus.regions.entities.Region;
import org.bukkit.event.HandlerList;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class RegionEvent extends RCRegionEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final Region region;

    protected RegionEvent(Region region) {
        this.region = region;
    }
}

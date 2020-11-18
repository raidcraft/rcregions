package net.silthus.regions.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.ebean.BaseEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@Accessors(fluent = true)
@Table(name = "sregions_players")
public class RegionPlayer extends BaseEntity {

    private String name;
    @OneToMany
    private List<Region> regions = new ArrayList<>();

    public Optional<Player> getBukkitPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(id()));
    }
}

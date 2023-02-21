package net.emc.emcw.objects.raw;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RawPlayer {
    Integer x, y, z;
    String name, account, world;
}

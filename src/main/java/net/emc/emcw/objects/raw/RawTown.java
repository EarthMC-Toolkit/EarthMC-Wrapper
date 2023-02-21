package net.emc.emcw.objects.raw;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RawTown {
    String desc, label, fillcolor, color;
    List<Integer> x, z;
}
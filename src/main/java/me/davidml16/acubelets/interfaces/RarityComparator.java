package me.davidml16.acubelets.interfaces;

import me.davidml16.acubelets.objects.Cubelet;
import me.davidml16.acubelets.objects.Rarity;

import java.util.Comparator;

public class RarityComparator implements Comparator<Rarity> {

    @Override
    public int compare(Rarity o1, Rarity o2) {
        return (int) (o2.getChance() - o1.getChance());
    }

}
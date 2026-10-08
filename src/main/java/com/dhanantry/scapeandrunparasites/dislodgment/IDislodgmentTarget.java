package com.dhanantry.scapeandrunparasites.dislodgment;

/**
 * Implemented by parasite entities (EntityParasiteBase and the Inhoo variants) so the dislodgment system can
 * toggle their per-entity flags, replacing the public disloNumberX fields of 1.12.2.
 * <p>Boolean positions (2,3,4,6,7,8,9,18,20,21,22) receive 1 / 0. Timed positions (11,15,16,17,19) receive
 * {@code seconds * 20 + 50} ticks on start and 0 on end. Position 15 additionally toggles the "dislo 15" synced flag;
 * position 16 additionally snapshots the walked distance (done by the implementing entity).</p>
 */
public interface IDislodgmentTarget {
    void setDislodgment(int position, int value);
}

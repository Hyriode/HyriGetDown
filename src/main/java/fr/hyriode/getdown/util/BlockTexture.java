package fr.hyriode.getdown.util;

import org.bukkit.Material;

/**
 * Created by AstFaster
 * on 24/07/2022 at 17:47
 */
public class BlockTexture {

    private Material material;
    private byte data;

    public BlockTexture(Material material, byte data) {
        this.material = material;
        this.data = data;
    }

    public BlockTexture(Material material) {
        this(material, (byte) 0);
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public byte getData() {
        return this.data;
    }

    public void setData(byte data) {
        this.data = data;
    }

}

/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.storage;

import io.swagger.annotations.ApiModelProperty;

/**
 * HostDiskDimensionsLba
 *
 * @since 2019 -09-16
 */
public class HostDiskDimensionsLba {

    @ApiModelProperty(value = "块数量", example = "933593088", required = true)
    private long block;
    @ApiModelProperty(value = "每块大小(Byte)", example = "512", required = true)
    private int blockSize;

    /**
     * Gets block.
     *
     * @return the block
     */
    public long getBlock() {
        return block;
    }

    /**
     * Sets block.
     *
     * @param block the block
     * @return the block
     */
    public HostDiskDimensionsLba setBlock(long block) {
        this.block = block;
        return this;
    }

    /**
     * Gets block size.
     *
     * @return the block size
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * Sets block size.
     *
     * @param blockSize the block size
     * @return the block size
     */
    public HostDiskDimensionsLba setBlockSize(int blockSize) {
        this.blockSize = blockSize;
        return this;
    }
}

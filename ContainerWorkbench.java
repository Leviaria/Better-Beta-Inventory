package net.minecraft.src;

import java.util.List;

public class ContainerWorkbench extends Container
{

    public ContainerWorkbench(InventoryPlayer inventoryplayer, World world, int i, int j, int k)
    {
        craftMatrix = new InventoryCrafting(this, 3, 3);
        craftResult = new InventoryCraftResult();
        field_20133_c = world;
        field_20132_h = i;
        field_20131_i = j;
        field_20130_j = k;
        addSlot(new SlotCrafting(inventoryplayer.player, craftMatrix, craftResult, 0, 124, 35));
        for(int l = 0; l < 3; l++)
        {
            for(int k1 = 0; k1 < 3; k1++)
            {
                addSlot(new Slot(craftMatrix, k1 + l * 3, 30 + k1 * 18, 17 + l * 18));
            }

        }

        for(int i1 = 0; i1 < 3; i1++)
        {
            for(int l1 = 0; l1 < 9; l1++)
            {
                addSlot(new Slot(inventoryplayer, l1 + i1 * 9 + 9, 8 + l1 * 18, 84 + i1 * 18));
            }

        }

        for(int j1 = 0; j1 < 9; j1++)
        {
            addSlot(new Slot(inventoryplayer, j1, 8 + j1 * 18, 142));
        }

        onCraftMatrixChanged(craftMatrix);
    }

    public void onCraftMatrixChanged(IInventory iinventory)
    {
        craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix));
    }

    public void onCraftGuiClosed(EntityPlayer entityplayer)
    {
        super.onCraftGuiClosed(entityplayer);
        for(int i = 0; i < 9; i++)
        {
            ItemStack itemstack = craftMatrix.getStackInSlot(i);
            if(itemstack == null)
            {
                continue;
            }
            craftMatrix.setInventorySlotContents(i, null);
            if(!entityplayer.inventory.addItemStackToInventory(itemstack))
            {
                entityplayer.dropPlayerItem(itemstack);
            }
        }

    }

    public boolean isUsableByPlayer(EntityPlayer entityplayer)
    {
        if(field_20133_c.getBlockId(field_20132_h, field_20131_i, field_20130_j) != Block.workbench.blockID)
        {
            return false;
        }
        return entityplayer.getDistanceSq((double)field_20132_h + 0.5D, (double)field_20131_i + 0.5D, (double)field_20130_j + 0.5D) <= 64D;
    }

    private int countCraftableBatches(EntityPlayer entityplayer)
    {
        ItemStack result = craftResult.getStackInSlot(0);
        if(result == null)
        {
            return 0;
        }
        int maxBatches = 64;
        for(int gridSlot = 0; gridSlot < craftMatrix.getSizeInventory(); gridSlot++)
        {
            ItemStack ingredient = craftMatrix.getStackInSlot(gridSlot);
            if(ingredient == null)
            {
                continue;
            }
            int available = 0;
            for(int invSlot = 0; invSlot < entityplayer.inventory.mainInventory.length; invSlot++)
            {
                ItemStack invStack = entityplayer.inventory.mainInventory[invSlot];
                if(invStack != null && invStack.itemID == ingredient.itemID
                        && (!invStack.getHasSubtypes() || invStack.getItemDamage() == ingredient.getItemDamage()))
                {
                    available += invStack.stackSize;
                }
            }
            available += ingredient.stackSize - 1;
            int batches = available / ingredient.stackSize;
            if(batches < maxBatches)
            {
                maxBatches = batches;
            }
        }
        return maxBatches;
    }

    private int countFreeAndMergeSpace(EntityPlayer entityplayer, ItemStack result)
    {
        int space = 0;
        for(int i = 0; i < entityplayer.inventory.mainInventory.length; i++)
        {
            ItemStack slot = entityplayer.inventory.mainInventory[i];
            if(slot == null)
            {
                space += result.getMaxStackSize();
            }
            else if(slot.itemID == result.itemID
                    && (!slot.getHasSubtypes() || slot.getItemDamage() == result.getItemDamage())
                    && slot.stackSize < slot.getMaxStackSize())
            {
                space += slot.getMaxStackSize() - slot.stackSize;
            }
        }
        return space;
    }

    public ItemStack getStackInSlot(int i)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)slots.get(i);
        if(slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if(i == 0)
            {
                func_28125_a(itemstack1, 10, 46, true);
            } else
            if(i >= 10 && i < 37)
            {
                func_28125_a(itemstack1, 37, 46, false);
            } else
            if(i >= 37 && i < 46)
            {
                func_28125_a(itemstack1, 10, 37, false);
            } else
            {
                func_28125_a(itemstack1, 10, 46, false);
            }
            if(itemstack1.stackSize == 0)
            {
                slot.putStack(null);
            } else
            {
                slot.onSlotChanged();
            }
            if(itemstack1.stackSize != itemstack.stackSize)
            {
                slot.onPickupFromSlot(itemstack1);
            } else
            {
                return null;
            }
        }
        return itemstack;
    }

    public ItemStack func_27280_a(int slotIndex, int mouseButton, boolean isShiftClick, EntityPlayer entityplayer)
    {
        if(isShiftClick && slotIndex == 0 && craftResult.getStackInSlot(0) != null)
        {
            ItemStack resultTemplate = craftResult.getStackInSlot(0);
            int batches = countCraftableBatches(entityplayer);
            int spaceInInventory = countFreeAndMergeSpace(entityplayer, resultTemplate);
            int maxBySpace = spaceInInventory / resultTemplate.stackSize;
            if(maxBySpace < batches)
            {
                batches = maxBySpace;
            }
            for(int pass = 0; pass < batches; pass++)
            {
                if(craftResult.getStackInSlot(0) == null)
                {
                    break;
                }
                ItemStack crafted = craftResult.getStackInSlot(0).copy();
                if(!entityplayer.inventory.addItemStackToInventory(crafted))
                {
                    break;
                }
                SlotCrafting craftSlot = (SlotCrafting)slots.get(0);
                craftSlot.onPickupFromSlot(craftResult.getStackInSlot(0));
                onCraftMatrixChanged(craftMatrix);
            }
            return null;
        }
        return super.func_27280_a(slotIndex, mouseButton, isShiftClick, entityplayer);
    }

    public InventoryCrafting craftMatrix;
    public IInventory craftResult;
    private World field_20133_c;
    private int field_20132_h;
    private int field_20131_i;
    private int field_20130_j;
}

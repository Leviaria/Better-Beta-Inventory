package net.minecraft.src;

import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public abstract class GuiContainer extends GuiScreen
{

    public GuiContainer(Container container)
    {
        xSize = 176;
        ySize = 166;
        inventorySlots = container;
    }

    public void initGui()
    {
        super.initGui();
        mc.thePlayer.craftingInventory = inventorySlots;
    }

    public void drawScreen(int i, int j, float f)
    {
        lastMouseX = i;
        lastMouseY = j;
        drawDefaultBackground();
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        drawGuiContainerBackgroundLayer(f);
        GL11.glPushMatrix();
        GL11.glRotatef(120F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(k, l, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        Slot slot = null;
        for(int i1 = 0; i1 < inventorySlots.slots.size(); i1++)
        {
            Slot slot1 = (Slot)inventorySlots.slots.get(i1);
            drawSlotInventory(slot1);
            if(getIsMouseOverSlot(slot1, i, j))
            {
                slot = slot1;
                GL11.glDisable(2896 /*GL_LIGHTING*/);
                GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
                int j1 = slot1.xDisplayPosition;
                int l1 = slot1.yDisplayPosition;
                drawGradientRect(j1, l1, j1 + 16, l1 + 16, 0x80ffffff, 0x80ffffff);
                GL11.glEnable(2896 /*GL_LIGHTING*/);
                GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
            }
        }

        InventoryPlayer inventoryplayer = mc.thePlayer.inventory;
        if(inventoryplayer.getItemStack() != null)
        {
            GL11.glTranslatef(0.0F, 0.0F, 32F);
            itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, inventoryplayer.getItemStack(), i - k - 8, j - l - 8);
            itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, inventoryplayer.getItemStack(), i - k - 8, j - l - 8);
        }
        GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(2896 /*GL_LIGHTING*/);
        GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        drawGuiContainerForegroundLayer();
        if(inventoryplayer.getItemStack() == null && slot != null && slot.getHasStack())
        {
            String s = (new StringBuilder()).append("").append(StringTranslate.getInstance().translateNamedKey(slot.getStack().getItemName())).toString().trim();
            if(s.length() > 0)
            {
                int k1 = (i - k) + 12;
                int i2 = j - l - 12;
                int j2 = fontRenderer.getStringWidth(s);
                drawGradientRect(k1 - 3, i2 - 3, k1 + j2 + 3, i2 + 8 + 3, 0xc0000000, 0xc0000000);
                fontRenderer.drawStringWithShadow(s, k1, i2, -1);
            }
        }
        GL11.glPopMatrix();
        super.drawScreen(i, j, f);
        GL11.glEnable(2896 /*GL_LIGHTING*/);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
    }

    protected void drawGuiContainerForegroundLayer()
    {
    }

    protected abstract void drawGuiContainerBackgroundLayer(float f);

    private void drawSlotInventory(Slot slot)
    {
        int i = slot.xDisplayPosition;
        int j = slot.yDisplayPosition;
        ItemStack itemstack = slot.getStack();
        if(itemstack == null)
        {
            int k = slot.getBackgroundIconIndex();
            if(k >= 0)
            {
                GL11.glDisable(2896 /*GL_LIGHTING*/);
                mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/gui/items.png"));
                drawTexturedModalRect(i, j, (k % 16) * 16, (k / 16) * 16, 16, 16);
                GL11.glEnable(2896 /*GL_LIGHTING*/);
                return;
            }
        }
        itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, itemstack, i, j);
        itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, itemstack, i, j);
    }

    private Slot getSlotAtPosition(int i, int j)
    {
        for(int k = 0; k < inventorySlots.slots.size(); k++)
        {
            Slot slot = (Slot)inventorySlots.slots.get(k);
            if(getIsMouseOverSlot(slot, i, j))
            {
                return slot;
            }
        }

        return null;
    }

    private boolean getIsMouseOverSlot(Slot slot, int i, int j)
    {
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        i -= k;
        j -= l;
        return i >= slot.xDisplayPosition - 1 && i < slot.xDisplayPosition + 16 + 1 && j >= slot.yDisplayPosition - 1 && j < slot.yDisplayPosition + 16 + 1;
    }

    private int getHotbarIndexForScancode(int scancode)
    {
        switch(scancode)
        {
            case 2:  return 0;
            case 3:  return 1;
            case 4:  return 2;
            case 5:  return 3;
            case 6:  return 4;
            case 7:  return 5;
            case 8:  return 6;
            case 9:  return 7;
            case 10: return 8;
            default: return -1;
        }
    }

    private int findHotbarSlotIndex(int hotbarIndex)
    {
        InventoryPlayer inv = mc.thePlayer.inventory;
        for(int i = 0; i < inventorySlots.slots.size(); i++)
        {
            Slot s = (Slot)inventorySlots.slots.get(i);
            if(s.getInventory() == inv && s.getSlotIndex() == hotbarIndex)
            {
                return i;
            }
        }
        return -1;
    }

    private void swapSlotWithHotbar(Slot hoveredSlot, int hotbarIndex)
    {
        InventoryPlayer inv = mc.thePlayer.inventory;
        ItemStack slotStack = hoveredSlot.getStack();
        ItemStack hotbarStack = inv.mainInventory[hotbarIndex];

        if(slotStack == null && hotbarStack == null)
        {
            return;
        }
        if(slotStack == null)
        {
            if(!hoveredSlot.isItemValid(hotbarStack))
            {
                return;
            }
            int hotbarContainerSlot = findHotbarSlotIndex(hotbarIndex);
            if(hotbarContainerSlot == -1)
            {
                return;
            }
            mc.playerController.func_27174_a(inventorySlots.windowId, hotbarContainerSlot, 0, false, mc.thePlayer);
            mc.playerController.func_27174_a(inventorySlots.windowId, hoveredSlot.slotNumber, 0, false, mc.thePlayer);
            if(inv.getItemStack() != null)
            {
                mc.playerController.func_27174_a(inventorySlots.windowId, hotbarContainerSlot, 0, false, mc.thePlayer);
            }
            return;
        }
        if(hotbarStack != null && !hoveredSlot.isItemValid(hotbarStack))
        {
            return;
        }
        int hotbarContainerSlot = findHotbarSlotIndex(hotbarIndex);
        if(hotbarContainerSlot == -1)
        {
            return;
        }
        mc.playerController.func_27174_a(inventorySlots.windowId, hoveredSlot.slotNumber, 0, false, mc.thePlayer);
        mc.playerController.func_27174_a(inventorySlots.windowId, hotbarContainerSlot, 0, false, mc.thePlayer);
        if(inv.getItemStack() != null)
        {
            mc.playerController.func_27174_a(inventorySlots.windowId, hoveredSlot.slotNumber, 0, false, mc.thePlayer);
        }
    }

    private void dropSingleFromSlot(Slot slot)
    {
        if(slot == null || !slot.getHasStack())
        {
            return;
        }
        mc.playerController.func_27174_a(inventorySlots.windowId, slot.slotNumber, 0, false, mc.thePlayer);
        mc.playerController.func_27174_a(inventorySlots.windowId, -999, 1, false, mc.thePlayer);
        if(mc.thePlayer.inventory.getItemStack() != null)
        {
            mc.playerController.func_27174_a(inventorySlots.windowId, slot.slotNumber, 0, false, mc.thePlayer);
        }
    }

    private void dropFullStackFromSlot(Slot slot)
    {
        if(slot == null || !slot.getHasStack())
        {
            return;
        }
        mc.playerController.func_27174_a(inventorySlots.windowId, slot.slotNumber, 0, false, mc.thePlayer);
        mc.playerController.func_27174_a(inventorySlots.windowId, -999, 0, false, mc.thePlayer);
    }

    private boolean tryEquipArmorFromSlot(Slot sourceSlot)
    {
        if(sourceSlot == null || !sourceSlot.getHasStack())
        {
            return false;
        }
        ItemStack stack = sourceSlot.getStack();
        if(!(stack.getItem() instanceof ItemArmor))
        {
            return false;
        }
        for(int i = 0; i < inventorySlots.slots.size(); i++)
        {
            Slot candidate = (Slot)inventorySlots.slots.get(i);
            if((candidate instanceof SlotArmor) && candidate.isItemValid(stack) && !candidate.getHasStack())
            {
                candidate.putStack(stack.copy());
                sourceSlot.putStack(null);
                mc.thePlayer.inventory.onInventoryChanged();
                return true;
            }
        }
        return false;
    }

    protected void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
        if(k == 0 || k == 1)
        {
            Slot slot = getSlotAtPosition(i, j);
            int l = (width - xSize) / 2;
            int i1 = (height - ySize) / 2;
            boolean flag = i < l || j < i1 || i >= l + xSize || j >= i1 + ySize;
            int j1 = -1;
            if(slot != null)
            {
                j1 = slot.slotNumber;
            }
            if(flag)
            {
                j1 = -999;
            }
            if(j1 != -1)
            {
                boolean shiftHeld = j1 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                if(k == 1 && !shiftHeld && slot != null && slot.getHasStack()
                        && mc.thePlayer.inventory.getItemStack() == null
                        && tryEquipArmorFromSlot(slot))
                {
                    return;
                }
                mc.playerController.func_27174_a(inventorySlots.windowId, j1, k, shiftHeld, mc.thePlayer);
            }
        }
    }

    protected void mouseMovedOrUp(int i, int j, int k)
    {
        if(k != 0);
    }

    protected void keyTyped(char c, int i)
    {
        if(i == 1 || i == mc.gameSettings.keyBindInventory.keyCode)
        {
            mc.thePlayer.closeScreen();
            return;
        }

        int hotbarIndex = getHotbarIndexForScancode(i);
        if(hotbarIndex != -1)
        {
            Slot hoveredSlot = getSlotAtPosition(lastMouseX, lastMouseY);
            if(hoveredSlot != null)
            {
                swapSlotWithHotbar(hoveredSlot, hotbarIndex);
            }
            return;
        }

        if(i == mc.gameSettings.keyBindDrop.keyCode)
        {
            Slot hoveredSlot = getSlotAtPosition(lastMouseX, lastMouseY);
            if(hoveredSlot != null && hoveredSlot.getHasStack())
            {
                boolean shiftHeld = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
                if(shiftHeld)
                {
                    dropFullStackFromSlot(hoveredSlot);
                }
                else
                {
                    dropSingleFromSlot(hoveredSlot);
                }
            }
        }
    }

    public void onGuiClosed()
    {
        if(mc.thePlayer == null)
        {
            return;
        } else
        {
            mc.playerController.func_20086_a(inventorySlots.windowId, mc.thePlayer);
            return;
        }
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }

    public void updateScreen()
    {
        super.updateScreen();
        if(!mc.thePlayer.isEntityAlive() || mc.thePlayer.isDead)
        {
            mc.thePlayer.closeScreen();
        }
    }

    private static RenderItem itemRenderer = new RenderItem();
    protected int xSize;
    protected int ySize;
    public Container inventorySlots;
    private int lastMouseX;
    private int lastMouseY;

}

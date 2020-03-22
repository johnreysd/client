package me.zeroeightsix.kami.gui.kami.theme.kami;

import me.zeroeightsix.kami.KamiMod;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.gui.rgui.render.AbstractComponentUI;
import me.zeroeightsix.kami.gui.rgui.render.font.FontRenderer;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.module.modules.gui.ActiveModules;
import me.zeroeightsix.kami.util.Wrapper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static me.zeroeightsix.kami.util.ColourConverter.toF;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glDisable;

/**
 * Created by 086 on 4/08/2017.
 * Updated by S-B99 on 20/03/19
 */
public class KamiActiveModulesUI extends AbstractComponentUI<me.zeroeightsix.kami.gui.kami.component.ActiveModules> {
    ActiveModules activeMods;
    @Override
    public void renderComponent(me.zeroeightsix.kami.gui.kami.component.ActiveModules component, FontRenderer f) {
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        FontRenderer renderer = Wrapper.getFontRenderer();
        List<Module> mods = ModuleManager.getModules().stream()
                .filter(Module::isEnabled)
                .filter(Module::isOnArray)
                .sorted(Comparator.comparing(module -> renderer.getStringWidth(module.getName() + (module.getHudInfo() == null ? "" : module.getHudInfo() + " ")) * (component.sort_up ? -1 : 1)))
                .collect(Collectors.toList());

        final int[] y = {2};
        activeMods = (ActiveModules) ModuleManager.getModuleByName("ActiveModules");

        if (component.getParent().getY() < 26 && Wrapper.getPlayer().getActivePotionEffects().size() > 0 && component.getParent().getOpacity() == 0)
            y[0] = Math.max(component.getParent().getY(), 26 - component.getParent().getY());

        final float[] hue = {(System.currentTimeMillis() % (360 * activeMods.getRainbowSpeed())) / (360f * activeMods.getRainbowSpeed())};

        Function<Integer, Integer> xFunc;
        switch (component.getAlignment()) {
            case RIGHT:
                xFunc = i -> component.getWidth() - i;
                break;
            case CENTER:
                xFunc = i -> component.getWidth() / 2 - i / 2;
                break;
            case LEFT:
            default:
                xFunc = i -> 0;
                break;
        }

        for (int i = 0 ; i < mods.size() ; i++) {
            Module module = mods.get(i);
            int rgb;

            if (activeMods.mode.getValue().equals(ActiveModules.Mode.RAINBOW)) {
                rgb = Color.HSBtoRGB(hue[0], toF(activeMods.saturationR.getValue()), toF(activeMods.brightnessR.getValue()));
            } else if (activeMods.mode.getValue().equals(ActiveModules.Mode.CATEGORY)) {
                rgb = ActiveModules.getCategoryColour(module);
            } else if (activeMods.mode.getValue().equals(ActiveModules.Mode.CUSTOM)) {
                rgb = Color.HSBtoRGB(toF(activeMods.hueC.getValue()), toF(activeMods.saturationC.getValue()), toF(activeMods.brightnessC.getValue()));
            } else {
                rgb = activeMods.getInfoColour(i);
            }

            String s = module.getHudInfo();
            String text = module.getName() + (s == null ? "" : " " + KamiMod.colour + "7" + s);
            int textWidth = renderer.getStringWidth(text);
            int textHeight = renderer.getFontHeight() + 1;
            int red = (rgb >> 16) & 0xFF;
            int green = (rgb >> 8) & 0xFF;
            int blue = rgb & 0xFF;

            renderer.drawStringWithShadow(xFunc.apply(textWidth), y[0], red, green, blue, text);
            hue[0] += .02f;
            y[0] += textHeight;
        }

        component.setHeight(y[0]);

        GL11.glEnable(GL11.GL_CULL_FACE);
        glDisable(GL_BLEND);
    }

    @Override
    public void handleSizeComponent(me.zeroeightsix.kami.gui.kami.component.ActiveModules component) {
        component.setWidth(100);
        component.setHeight(100);
    }
}

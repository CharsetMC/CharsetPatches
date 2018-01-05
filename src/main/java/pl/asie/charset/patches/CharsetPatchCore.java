package pl.asie.charset.patches;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import pl.asie.charset.patchwork.CharsetPatchwork;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;

@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions({"pl.asie.charset.patches", "pl.asie.charset.patchwork"})
public class CharsetPatchCore implements IFMLLoadingPlugin {
	@Override
	public String[] getASMTransformerClass() {
		return new String[]{"pl.asie.charset.patches.CharsetPatchTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Nullable
	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		Configuration config = new Configuration(new File(new File(new File("config"), "charset"), "patches.cfg"));

		CharsetPatchwork.LASER_REDSTONE = config.getBoolean("laserRedstone", "patches", true, "Allows lasers to act as redstone input.");
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}

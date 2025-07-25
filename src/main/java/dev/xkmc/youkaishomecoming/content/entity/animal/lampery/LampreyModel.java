package dev.xkmc.youkaishomecoming.content.entity.animal.lampery;

import dev.xkmc.youkaishomecoming.init.YoukaisHomecoming;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class LampreyModel<T extends Entity> extends HierarchicalModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(YoukaisHomecoming.loc("lamprey"), "main");

	public static LayerDefinition createBodyLayer() {

		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition front = partdefinition.addOrReplaceChild("front", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.81F, -8.95F, 2.0F, 1.0F, 3.0F, new CubeDeformation(-0.3F))
				.texOffs(0, 14).addBox(-1.0F, -1.5F, -9.0F, 2.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.5F, 1.0F));

		PartDefinition back = partdefinition.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(0, 11).addBox(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.5F, 1.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	public final ModelPart root;
	public final ModelPart back;

	public LampreyModel(ModelPart root) {
		this.root = root;
		this.back = root.getChild("back");
	}

	@Override
	public ModelPart root() {
		return root;
	}

	public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
		float f = 1.0F;
		if (!pEntity.isInWater()) {
			f = 1.5F;
		}
		this.back.yRot = -f * 0.45F * Mth.sin(0.6F * pAgeInTicks);
	}

}

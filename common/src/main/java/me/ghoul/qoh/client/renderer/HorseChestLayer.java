package me.ghoul.qoh.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ghoul.qoh.interfaces.IHorseFeature;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

// Credit to LieOnLion for help with this - Modified by Ghoul
public class HorseChestLayer<T extends LivingEntity, M extends EntityModel<T>>
        extends RenderLayer<T, M> {

    private static final ResourceLocation CHEST =
            ResourceLocation.fromNamespaceAndPath("qoh", "textures/layer/chest.png");
    protected static final float HALF_PI = (float) Math.PI / 2.f;

    private final ModelPart chestModel;

    public HorseChestLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);

        PartDefinition partDefinition = new MeshDefinition().getRoot();
        CubeListBuilder cubeListBuilder =
                CubeListBuilder.create().texOffs(0, 0).addBox(-4, 0, -2, 8, 8, 3);
        partDefinition.addOrReplaceChild(
                "right_chest",
                cubeListBuilder,
                PartPose.offsetAndRotation(-7, 3, 5, 0, -HALF_PI, 0));
        partDefinition.addOrReplaceChild(
                "left_chest", cubeListBuilder, PartPose.offsetAndRotation(6, 3, 5, 0, -HALF_PI, 0));

        this.chestModel = partDefinition.bake(22, 11);
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int i,
            T entity,
            float f,
            float g,
            float h,
            float j,
            float k,
            float l) {
        if (!((IHorseFeature) entity).qoh$hasChest()) {
            return;
        }

        chestModel.render(
                poseStack,
                multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(CHEST)),
                i,
                LivingEntityRenderer.getOverlayCoords(entity, 0.f));
    }
}

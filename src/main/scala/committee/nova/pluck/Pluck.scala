package committee.nova.pluck

import committee.nova.pluck.Pluck.MODID
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.passive.EntitySheep
import net.minecraft.init.Blocks
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import java.util.Random
import scala.collection.mutable.ArrayBuffer

@Mod(modid = MODID, useMetadata = true, modLanguage = "scala")
@EventBusSubscriber
object Pluck {
  final val MODID = "pluck"

  @SubscribeEvent
  def onHurt(event: LivingHurtEvent): Unit = {
    val living = event.getEntityLiving
    if (living.world.isRemote) return
    if (!living.isInstanceOf[EntitySheep]) return
    val s = living.asInstanceOf[EntitySheep]
    if (s.getSheared || s.isChild) return
    val world = s.world
    val drops = shearSheepSilently(s, world.rand)
    val rand = new Random()
    val f = 0.7F
    val pos = s.getPosition
    for (r <- drops) {
      val d = (rand.nextFloat * f).toDouble + (1.0F - f).toDouble * 0.5D
      val d1 = (rand.nextFloat * f).toDouble + (1.0F - f).toDouble * 0.5D
      val d2 = (rand.nextFloat * f).toDouble + (1.0F - f).toDouble * 0.5D
      val entityItem = new EntityItem(world, pos.getX.toDouble + d, pos.getY.toDouble + d1, pos.getZ.toDouble + d2, r)
      entityItem.setDefaultPickupDelay()
      world.spawnEntity(entityItem)
    }
  }

  private def shearSheepSilently(sheep: EntitySheep, random: Random): Array[ItemStack] = {
    sheep.setSheared(true)
    val i = 1 + random.nextInt(3)
    val ret = new ArrayBuffer[ItemStack]
    for (_ <- 0 until i) {
      ret.+=(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, sheep.getFleeceColor.getMetadata))
    }
    ret.toArray
  }
}

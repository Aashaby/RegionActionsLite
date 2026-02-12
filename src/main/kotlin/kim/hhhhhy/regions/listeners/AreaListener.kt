package kim.hhhhhy.regions.listeners

import kim.hhhhhy.regions.data.AreaSettings
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.isBlockMovement
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object AreaListener {
    val playerSet = mutableSetOf<Pair<String, String>>()

    private data class BlockPos(val world: String, val x: Int, val y: Int, val z: Int)

    private val fakedPortalBlocks = ConcurrentHashMap<UUID, MutableSet<BlockPos>>()
    private val fakedPortalRegionKeys = ConcurrentHashMap<UUID, MutableSet<String>>()

    private val airBlockData: BlockData by lazy {
        Bukkit.createBlockData(Material.AIR)
    }

    fun fakePortalBlocksInBox(
        player: Player,
        worldName: String,
        x1: Int,
        y1: Int,
        z1: Int,
        x2: Int,
        y2: Int,
        z2: Int
    ) {
        fakePortalBlocksInBoxOnce(player, null, worldName, x1, y1, z1, x2, y2, z2)
    }

    fun fakePortalBlocksInBoxOnce(
        player: Player,
        regionKey: String?,
        worldName: String,
        x1: Int,
        y1: Int,
        z1: Int,
        x2: Int,
        y2: Int,
        z2: Int
    ) {
        if (regionKey != null) {
            val keys = fakedPortalRegionKeys.computeIfAbsent(player.uniqueId) { ConcurrentHashMap.newKeySet() }
            if (!keys.add(regionKey)) {
                return
            }
        }
        val world = Bukkit.getWorld(worldName) ?: return
        val minX = kotlin.math.min(x1, x2)
        val maxX = kotlin.math.max(x1, x2)
        val minY = kotlin.math.min(y1, y2)
        val maxY = kotlin.math.max(y1, y2)
        val minZ = kotlin.math.min(z1, z2)
        val maxZ = kotlin.math.max(z1, z2)

        val volume = (maxX - minX + 1).toLong() * (maxY - minY + 1).toLong() * (maxZ - minZ + 1).toLong()
        if (volume > 20000L) {
            return
        }

        val set = fakedPortalBlocks.computeIfAbsent(player.uniqueId) { ConcurrentHashMap.newKeySet() }
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val block = world.getBlockAt(x, y, z)
                    if (isPortalType(block.type)) {
                        val pos = BlockPos(world.name, x, y, z)
                        if (set.add(pos)) {
                            player.sendBlockChange(block.location, airBlockData)
                        } else {
                            player.sendBlockChange(block.location, airBlockData)
                        }
                    }
                }
            }
        }
    }

    fun restoreFakePortalBlocks(player: Player) {
        fakedPortalRegionKeys.remove(player.uniqueId)
        val set = fakedPortalBlocks.remove(player.uniqueId) ?: return
        set.forEach { pos ->
            val world = Bukkit.getWorld(pos.world) ?: return@forEach
            val block = world.getBlockAt(pos.x, pos.y, pos.z)
            player.sendBlockChange(block.location, block.blockData)
        }
    }

    private fun isPortalType(type: Material): Boolean {
        return type.name == "NETHER_PORTAL" || type.name == "END_PORTAL" || type.name == "PORTAL"
    }

    @SubscribeEvent
    fun onPlayerMove(e: PlayerMoveEvent) {
        val to = e.to ?: return
        if (!e.isBlockMovement()) {
            return
        }
        val player = e.player
        AreaSettings.check(player, to, e.from, e)
    }

    @SubscribeEvent
    fun onPlayerChangeWorld(e: PlayerChangedWorldEvent) {
        restoreFakePortalBlocks(e.player)
        AreaSettings.stopTick(e.player)
        AreaSettings.check(e.player, e.player.location, e.from.spawnLocation, e)
    }

    @SubscribeEvent
    fun onTeleport(e: PlayerTeleportEvent) {
        val from = e.from
        val to = e.to ?: return
        val player = e.player
        AreaSettings.check(player, to, from, e)
    }

    @SubscribeEvent
    fun onPlayerJoin(e: PlayerJoinEvent) {
        AreaSettings.check(e.player, e.player.location, e.player.location, e)
    }

    @SubscribeEvent
    fun onPlayerQuit(e: PlayerQuitEvent) {
        playerSet.removeAll { it.first == e.player.name }
        restoreFakePortalBlocks(e.player)
        AreaSettings.stopTick(e.player)
    }
}
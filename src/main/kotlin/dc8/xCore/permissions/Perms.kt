package dc8.xCore.permissions

object Perms {
    private const val BASE = "xcore."
    private const val ADMIN_BASE = "xcore.admin."
    object GM {
        private const val COMMAND_BASE = ADMIN_BASE + "gm."
        const val COMMAND_BASE_MODE = COMMAND_BASE + "mode."
        const val SURVIVAL = COMMAND_BASE_MODE + 0
        const val CREATIVE = COMMAND_BASE_MODE + 1
        const val ADVENTURE = COMMAND_BASE_MODE + 2
        const val SPECTATOR = COMMAND_BASE_MODE + 3
        const val OTHERS = COMMAND_BASE + "others"
    }
}

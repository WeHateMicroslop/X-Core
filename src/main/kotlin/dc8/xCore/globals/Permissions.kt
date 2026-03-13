package dc8.xCore.globals

object Permissions {
    private const val XCORE_BASE = "xcore."
    private const val USER_CMD = XCORE_BASE + "user." + "command."
    private const val ADMIN = XCORE_BASE + "admin."
    private const val ADMIN_CMD = ADMIN + "command."
    object Reload {
         const val RELOAD = ADMIN_CMD + "reload"
    }
    object GM {
        private const val BASE = ADMIN_CMD + "gm."
        const val BASE_MODE = BASE + "mode."
        const val SURVIVAL = BASE_MODE + 0
        const val CREATIVE = BASE_MODE + 1
        const val ADVENTURE = BASE_MODE + 2
        const val SPECTATOR = BASE_MODE + 3
        const val OTHERS = BASE + "others"
    }
    object Home {
        private const val HOME = "home."
        private const val BASE_ADMIN = ADMIN_CMD + HOME
        private const val BASE_USER = USER_CMD + HOME
        const val TELEPORT = BASE_USER + "teleport"
        const val SET = BASE_USER + "set"
        const val DELETE = BASE_USER + "delete"
        const val BYPASS_LIMIT = BASE_ADMIN + "bypasslimit"
        const val LOCATE = BASE_USER + "locate"
        const val LIST = BASE_USER + "list"
    }
}

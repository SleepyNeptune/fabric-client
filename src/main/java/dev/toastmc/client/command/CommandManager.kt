package dev.toastmc.client.command

import dev.toastmc.client.command.cmds.Help
import dev.toastmc.client.command.cmds.List
import dev.toastmc.client.command.cmds.Toggle
import dev.toastmc.client.util.MessageUtil
import java.util.*
import kotlin.collections.HashSet

class CommandManager () {
    var commands = HashSet<Command>()
    private val aliasMap = HashMap<String, Command>()

    fun init() {
        aliasMap.clear()
        commands.clear()
        register(Help(), List(), Toggle())
    }

    private fun register(vararg commands: Command) {
        for (command in commands) {
            this.commands.add(command)
            if (command.getAlias()?.isNotEmpty()!!) {
                for (com in command.getAlias()!!) {
                    aliasMap[com.toLowerCase()] = command
                }
            }
        }
    }

    fun executeCmd(name: String, args: Array<String>) {
        if (getCommand(name) != null) getCommand(name)!!.run(args) else MessageUtil.defaultErrorMessage()
    }

    /**
     * Gets a command from it's name
     */
    private fun getCommand(cmd: String?): Command? {
        val commandIter = commands.iterator()
        while (commandIter.hasNext()) {
            val next = commandIter.next()
            for (label in next.getLabel()!!) {
                if(label.toString().equals(cmd, ignoreCase = true)){
                    return next
                }
            }
            for (alias in next.getAlias()!!) {
                if (alias.equals(cmd, ignoreCase = true)) {
                    return next
                }
            }
        }
        return null
    }
}

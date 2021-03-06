package dev.toastmc.client.command.util.type

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import dev.toastmc.client.ToastClient
import dev.toastmc.client.module.Module
import net.minecraft.server.command.CommandSource
import net.minecraft.text.LiteralText
import java.util.*
import java.util.concurrent.CompletableFuture

class ModuleArgumentType<Module>(private val clazz: Class<Module>) : ArgumentType<Module> {

    val modules = ToastClient.MODULE_MANAGER.modules

    @Throws(CommandSyntaxException::class)
    override fun parse(reader: StringReader): Module {
        val string = reader.readUnquotedString()
        try {
            return clazz.cast(modules.filter { clazz.isInstance(it) }.first { it.label.equals(string, ignoreCase = true) })
        } catch (e: NoSuchElementException) {
            throw INVALID_MODULE_EXCEPTION.create(string)
        }
    }

    override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return CommandSource.suggestMatching(
                modules.filter { clazz.isInstance(it) }.map { it.label },
                builder
        )
    }

    override fun getExamples(): Collection<String> {
        return EXAMPLES
    }

    companion object {
        private val EXAMPLES: Collection<String> = listOf("HUD", "AutoTotem", "Flight")
        val INVALID_MODULE_EXCEPTION = DynamicCommandExceptionType { mod -> LiteralText("Unknown Module '$mod'") }
        fun getModule(): ModuleArgumentType<Module> = ModuleArgumentType(Module::class.java)
    }
}
package com.stleo.ott

/**
 * Deprecated alias for LeoApp for backwards compatibility with plugins.
 * Use LeoApp instead.
 */
@Deprecated(
    message = "AcraApplication is deprecated, use LeoApp instead",
    replaceWith = ReplaceWith("com.stleo.ott.LeoApp"),
    level = DeprecationLevel.ERROR
)
class AcraApplication {
	companion object {

		@Deprecated(
		    message = "AcraApplication is deprecated, use LeoApp instead",
		    replaceWith = ReplaceWith("com.stleo.ott.LeoApp.context"),
		    level = DeprecationLevel.ERROR
		)
		val context get() = LeoApp.context

		@Deprecated(
		    message = "AcraApplication is deprecated, use LeoApp instead",
		    replaceWith = ReplaceWith("com.stleo.ott.LeoApp.removeKeys(folder)"),
		    level = DeprecationLevel.ERROR
		)
		fun removeKeys(folder: String): Int? =
		    LeoApp.removeKeys(folder)

		@Deprecated(
		    message = "AcraApplication is deprecated, use LeoApp instead",
		    replaceWith = ReplaceWith("com.stleo.ott.LeoApp.setKey(path, value)"),
		    level = DeprecationLevel.ERROR
		)
		fun <T> setKey(path: String, value: T) =
			LeoApp.setKey(path, value)

		@Deprecated(
		    message = "AcraApplication is deprecated, use LeoApp instead",
		    replaceWith = ReplaceWith("com.stleo.ott.LeoApp.setKey(folder, path, value)"),
		    level = DeprecationLevel.ERROR
		)
		fun <T> setKey(folder: String, path: String, value: T) =
			LeoApp.setKey(folder, path, value)

		@Deprecated(
		    message = "AcraApplication is deprecated, use LeoApp instead",
		    replaceWith = ReplaceWith("com.stleo.ott.LeoApp.getKey(path, defVal)"),
		    level = DeprecationLevel.ERROR
		)
		inline fun <reified T : Any> getKey(path: String, defVal: T?): T? =
			LeoApp.getKey(path, defVal)

		@Deprecated(
		    message = "AcraApplication is deprecated, use LeoApp instead",
		    replaceWith = ReplaceWith("com.stleo.ott.LeoApp.getKey(path)"),
		    level = DeprecationLevel.ERROR
		)
		inline fun <reified T : Any> getKey(path: String): T? =
			LeoApp.getKey(path)

		@Deprecated(
		    message = "AcraApplication is deprecated, use LeoApp instead",
		    replaceWith = ReplaceWith("com.stleo.ott.LeoApp.getKey(folder, path)"),
		    level = DeprecationLevel.ERROR
		)
		inline fun <reified T : Any> getKey(folder: String, path: String): T? =
		    LeoApp.getKey(folder, path)

		@Deprecated(
		    message = "AcraApplication is deprecated, use LeoApp instead",
		    replaceWith = ReplaceWith("com.stleo.ott.LeoApp.getKey(folder, path, defVal)"),
		    level = DeprecationLevel.ERROR
		)
		inline fun <reified T : Any> getKey(folder: String, path: String, defVal: T?): T? =
			LeoApp.getKey(folder, path, defVal)
	}
}

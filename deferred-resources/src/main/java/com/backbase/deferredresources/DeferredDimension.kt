package com.backbase.deferredresources

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.DimenRes
import androidx.annotation.Px
import com.backbase.deferredresources.internal.resolveAttribute
import com.backbase.deferredresources.internal.toSize
import dev.drewhamilton.extracare.DataApi

/**
 * A wrapper for resolving an integer dimension on demand.
 */
public interface DeferredDimension {

    /**
     * Resolve the dimension as an integer pixel value for use as a size. The exact value is rounded, and non-zero exact
     * values are ensured to be at least one pixel in size.
     */
    @Px public fun resolveAsSize(context: Context): Int

    /**
     * Resolve the dimension as an integer pixel value. The exact value is truncated to an integer.
     */
    @Px public fun resolveAsOffset(context: Context): Int

    /**
     * Resolve the dimension's exact value.
     */
    @Px public fun resolveExact(context: Context): Float

    /**
     * A wrapper for a constant integer [pxValue].
     */
    @DataApi public class Constant(
        @Px private val pxValue: Float
    ) : DeferredDimension {

        /**
         * Convenience for initializing with an integer [pxValue].
         */
        public constructor(@Px pxValue: Int) : this(pxValue.toFloat())

        /**
         * Rounds [pxValue] to an integer. If [pxValue] is non-zero but rounds to zero, returns 1 pixel. [context] is
         * ignored.
         */
        @Px override fun resolveAsSize(context: Context): Int = pxValue.toSize()

        /**
         * Truncates [pxValue] to an integer pixel value. [context] is ignored.
         */
        @Px override fun resolveAsOffset(context: Context): Int = pxValue.toInt()

        /**
         * Returns [pxValue]. [context] is ignored.
         */
        override fun resolveExact(context: Context): Float = pxValue
    }

    /**
     * A wrapper for a [DimenRes] [resId].
     */
    @DataApi public class Resource(
        @DimenRes private val resId: Int
    ) : DeferredDimension {
        /**
         * Resolve [resId] to a pixel dimension size with the given [context].
         */
        @Px override fun resolveAsSize(context: Context): Int = context.resources.getDimensionPixelSize(resId)

        /**
         * Resolve [resId] to a pixel dimension offset with the given [context].
         */
        @Px override fun resolveAsOffset(context: Context): Int = context.resources.getDimensionPixelOffset(resId)

        /**
         * Resolve [resId] to a pixel dimension with the given [context].
         */
        @Px override fun resolveExact(context: Context): Float = context.resources.getDimension(resId)
    }

    /**
     * A wrapper for an [AttrRes] [resId] reference to a dimension.
     */
    @DataApi public class Attribute(
        @AttrRes private val resId: Int
    ) : DeferredDimension {

        // Re-used every time the dimension is resolved, for efficiency
        private val reusedTypedValue = TypedValue()

        /**
         * Resolve [resId] to a [Px] int for use as a size with the given [context]'s theme. The exact value is rounded,
         * and non-zero exact values are ensured to be at least one pixel in size.
         *
         * @throws IllegalArgumentException if [resId] cannot be resolved to a dimension.
         */
        @Px
        override fun resolveAsSize(context: Context): Int = resolveExact(context).toSize()

        /**
         * Resolve [resId] to a [Px] int for use as an offset with the given [context]'s theme. The exact value is
         * truncated to an integer.
         *
         * @throws IllegalArgumentException if [resId] cannot be resolved to a dimension.
         */
        @Px
        override fun resolveAsOffset(context: Context): Int = resolveExact(context).toInt()

        /**
         * Resolve [resId] to an exact [Px] value for use as a size with the given [context]'s theme.
         *
         * @throws IllegalArgumentException if [resId] cannot be resolved to a dimension.
         */
        @Px
        override fun resolveExact(context: Context): Float = context.resolveDimensionAttribute(resId)

        @Px
        private fun Context.resolveDimensionAttribute(@AttrRes resId: Int): Float =
            resolveAttribute(resId, "dimension", reusedTypedValue, TypedValue.TYPE_DIMENSION) {
                getDimension(resources.displayMetrics)
            }
    }
}

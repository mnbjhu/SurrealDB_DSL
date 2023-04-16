package uk.gibby.dsl.functions

import uk.gibby.dsl.types.Reference
import uk.gibby.dsl.types.booleanType

object Is {
    fun alphaNum(value: Reference<*>) = booleanType.createReference("is::alphanum(${value.getReference()})")
    fun alpha(value: Reference<*>) = booleanType.createReference("is::alpha(${value.getReference()})")
    fun ascii(value: Reference<*>) = booleanType.createReference("is::ascii(${value.getReference()})")
    fun domain(value: Reference<*>) = booleanType.createReference("is::domain(${value.getReference()})")
    fun email(value: Reference<*>) = booleanType.createReference("is::email(${value.getReference()})")
    fun hexadecimal(value: Reference<*>) = booleanType.createReference("is::hexadecimal(${value.getReference()})")
    fun latitude(value: Reference<*>) = booleanType.createReference("is::latitude(${value.getReference()})")
    fun longitude(value: Reference<*>) = booleanType.createReference("is::longitude(${value.getReference()})")
    fun numeric(value: Reference<*>) = booleanType.createReference("is::numeric(${value.getReference()})")
    fun semver(value: Reference<*>) = booleanType.createReference("is::semver(${value.getReference()})")
    fun uuid(value: Reference<*>) = booleanType.createReference("is::uuid(${value.getReference()})")
}
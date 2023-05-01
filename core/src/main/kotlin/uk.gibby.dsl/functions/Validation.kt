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
fun Reference<*>.isAlphaNum() = booleanType.createReference("alphanum(${getReference()})")
fun Reference<*>.isAlpha() = booleanType.createReference("alpha(${getReference()})")
fun Reference<*>.isAscii() = booleanType.createReference("ascii(${getReference()})")
fun Reference<*>.isDomain() = booleanType.createReference("domain(${getReference()})")
fun Reference<*>.isEmail() = booleanType.createReference("email(${getReference()})")
fun Reference<*>.isHexadecimal() = booleanType.createReference("hexadecimal(${getReference()})")
fun Reference<*>.isLatitude() = booleanType.createReference("latitude(${getReference()})")
fun Reference<*>.isLongitude() = booleanType.createReference("longitude(${getReference()})")
fun Reference<*>.isNumeric() = booleanType.createReference("numeric(${getReference()})")
fun Reference<*>.isSemver() = booleanType.createReference("semver(${getReference()})")
fun Reference<*>.isUuid() = booleanType.createReference("uuid(${getReference()})")

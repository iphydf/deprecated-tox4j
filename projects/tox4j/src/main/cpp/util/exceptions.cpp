#include "util/exceptions.h"

#include <cstdlib>
#include <sstream>


TOX4J_NORETURN void
tox4j_fatal_error (JNIEnv *env, char const *message)
{
  env->FatalError (message);
  std::abort ();
}


static std::string
fullMessage (jint instance_number, char const *message)
{
  std::ostringstream result;

  result << message << ", instance_number = " << instance_number;
  return result.str ();
}


static void
throw_exception (JNIEnv *env, jint instance_number, char const *class_name, char const *message)
{
  env->ThrowNew (env->FindClass (class_name), fullMessage (instance_number, message).c_str ());
}

void
throw_tox_killed_exception (JNIEnv *env, jint instance_number, char const *message)
{
  throw_exception (env, instance_number, "im/tox/tox4j/exceptions/ToxKilledException", message);
}

void
throw_illegal_state_exception (JNIEnv *env, jint instance_number, char const *message)
{
  throw_exception (env, instance_number, "java/lang/IllegalStateException", message);
}

void
throw_illegal_state_exception (JNIEnv *env, jint instance_number, std::string const &message)
{
  throw_exception (env, instance_number, "java/lang/IllegalStateException", message.c_str ());
}


void
throw_tox_exception (JNIEnv *env, char const *module, char const *prefix, char const *method, char const *code)
{
  std::string exceptionClassName = "im/tox/tox4j/";
  exceptionClassName += module;
  exceptionClassName += "/exceptions/Tox";
  exceptionClassName += prefix;
  exceptionClassName += method;
  exceptionClassName += "Exception";

  jclass exceptionClass = env->FindClass (exceptionClassName.c_str ());
  if (!exceptionClass)
    return;

  std::string enumClassName = exceptionClassName + "$" + code + "$";
  jclass enumClass = env->FindClass (enumClassName.c_str ());
  if (!enumClass)
    return;

  jfieldID enumCodeId = env->GetStaticFieldID (
    enumClass,
    "MODULE$",
    ("L" + enumClassName + ";").c_str ()
  );
  if (!enumCodeId)
    return;

  jobject enumCode = env->GetStaticObjectField (enumClass, enumCodeId);
  if (!enumCode)
    return;

  jmethodID constructor = env->GetStaticMethodID (
    exceptionClass,
    "apply",
    ("(L" + exceptionClassName + "$Code;Ljava/lang/String;)L" + exceptionClassName + ";").c_str ()
  );
  if (!constructor)
    return;

  jobject exception = env->CallStaticObjectMethod (
    exceptionClass,
    constructor,
    enumCode,
    env->NewStringUTF ("")
  );
  tox4j_assert (exception);

  env->Throw ((jthrowable)exception);
}

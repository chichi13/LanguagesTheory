.class public exercice1_2

.super java/lang/Object
.method public <init>()V
.limit stack 10
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method
.method public static main([Ljava/lang/String;)V
.limit stack 10
  ldc " This script will divide X by Y and will decrease Y by 1 each time until it reaches 0 where it will eventually stops. \n"
  invokestatic sal/Library/print(Ljava.lang.String;)V
  ldc " Enter value of X : "
  invokestatic sal/Library/print(Ljava.lang.String;)V
  invokestatic sal/Library/readInt()I
  istore 1
  ldc " Enter value of Y : "
  invokestatic sal/Library/print(Ljava.lang.String;)V
  invokestatic sal/Library/readInt()I
  istore 2
  ldc 0
  istore 3
NEXT_LOOP#0:
  ldc 1
  ifeq EXIT_LOOP#1
  iload 3
  ldc 1
  iadd
  istore 3
  iload 2
  ldc 0
  if_icmpeq TRUE_VAL#4
  iconst_0
  goto FALSE_VAL#5
TRUE_VAL#4:
  iconst_1
FALSE_VAL#5:
  ifeq NEXT_TEST#3
  goto EXIT_LOOP#1
  goto END_IF#2
NEXT_TEST#3:
END_IF#2:
  iload 1
  iload 2
  idiv
  istore 4
  iload 2
  ldc 1
  isub
  istore 2
  iload 1
  invokestatic sal/Library/print(I)V
  ldc " divided by  "
  invokestatic sal/Library/print(Ljava.lang.String;)V
  iload 2
  ldc 1
  iadd
  invokestatic sal/Library/print(I)V
  ldc " equals "
  invokestatic sal/Library/print(Ljava.lang.String;)V
  iload 4
  invokestatic sal/Library/print(I)V
  ldc "\n"
  invokestatic sal/Library/print(Ljava.lang.String;)V
  goto NEXT_LOOP#0
EXIT_LOOP#1:
    return
.limit locals 5
.end method


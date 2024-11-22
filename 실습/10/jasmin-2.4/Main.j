.class public Main
.super java/lang/Object
.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static fibo(I)I
.limit stack 32
.limit locals 32
iload_0
ifgt L1
iconst_0
ireturn
L1:
iload_0
iconst_1
if_icmpne L2
iconst_1
ireturn
L2:
iconst_0
istore_1
iconst_1
istore_2
iconst_2
istore_3
Loop:
iload_1
iload_2
iadd
istore 4
iload_2
istore_1
iload 4
istore_2
iinc 3 1
iload_3
iload_0
if_icmple Loop
iload_2
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 32
.limit locals 32
getstatic java/lang/System/out Ljava/io/PrintStream;
bipush 10
invokestatic Main/fibo(I)I
invokevirtual java/io/PrintStream/println(I)V
return
.end method
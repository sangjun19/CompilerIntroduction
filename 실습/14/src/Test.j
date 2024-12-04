.class public Test
.super java/lang/Object
; strandard initializer
.method public <init>()V
aload_0
invokenonvirtual java/lang/Object/<init>()V
return
.end method


.method public static main([Ljava/lang/String;)V
.limit stack 32
.limit locals 32
bipush 0
istore_0
return
.end method

.method public static add(II)I
.limit stack 32
.limit locals 32
iload_0
iload_1
iadd
ireturn
.end method


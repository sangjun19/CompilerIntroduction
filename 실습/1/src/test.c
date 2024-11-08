#include <stdio.h>
#include <stdlib.h>
void main()
{
int a;
int b;
int c;
char *as = malloc(100 * sizeof(char));
char *bs = malloc(100 * sizeof(char));
char *cs = malloc(100 * sizeof(char));
scanf("%d", &a);
scanf("%d", &b);
c = a + b;
printf("%d", c);
c = a - 5;
printf("%d", c);
a = 30;
as = "hello";
printf("%d", 10);
scanf("%s", bs);
printf("%s", bs);
printf("Helloworld");
printf("Hello^world");
printf("\n");
}

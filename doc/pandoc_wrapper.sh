
##pandoc /home/alexandre/code/elios/doc/*.txt -o /home/alexandre/code/elios/dist/doc/html/UserGuide.html -s

echo "pandoc wrapper ($*)"

pandoc $*

#!/bin/bash

kitty --hold -e "cd ../build/install/library && ./smartrun.sh bftsmart.intol.bftmap.BFTMapServer 0" &
kitty --hold -e "cd ../build/install/library && ./smartrun.sh bftsmart.intol.bftmap.BFTMapServer 1" &
kitty --hold -e "cd ../build/install/library && ./smartrun.sh bftsmart.intol.bftmap.BFTMapServer 2" &
kitty --hold -e "cd ../build/install/library && ./smartrun.sh bftsmart.intol.bftmap.BFTMapServer 3" &

kitty --hold -e "cd ../build/install/library && ./smartrun.sh bftsmart.intol.bftmap.BFTMapInteractiveClient 4" &
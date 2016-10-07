declare -a uriArr1
declare -a uriArr2
url=$1
uriArr1=(`cat $2`)
uriArr2=(`cat $3`)

for ((i = 0; i < ${#uriArr1[@]}; i++))
    do
        for ((j = 0; j < ${#uriArr2[@]}; j++))
            do
                sURI=${uriArr1[$i]}
                eURI=${uriArr2[$j]}
                echo "$sURI, $eURI, " >> lst_i4.txt
                sh i4_count.sh "$url" "$sURI" "$eURI" >> lst_i4.txt
                printf '\n' >> lst_i4.txt
            done
    done
echo Finished!
declare -a classNames
classNames=(`cat $1`)

for ((i = 0; i < ${#classNames[@]}; i++))
    do
                classA=${classNames[$i]}
                echo "Processing ${classA}"
                sh cnp.sh ${classA} >> class_num_properties.ttl
                printf '\n\n' >> class_num_properties.ttl
    done
echo Finished!

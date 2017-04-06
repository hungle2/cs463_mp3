
def main():
	addr_dict = {}
	max_trans_value = [0,0,0]
	max_trans_address = ["","",""]
	with open('userGraph.txt','r') as f:
		for line in f:
			in_id,out_id,value = line.split(",")
			if out_id not in addr_dict:
				addr_dict[out_id] = int(value)
			else:
				addr_dict[out_id] = addr_dict[out_id] + int(value)

			if max_trans_value[0] < int(value):
				max_trans_value[2] = max_trans_value[1]
				max_trans_value[1] = max_trans_value[0]
				max_trans_value[0] = int(value)

				max_trans_address[2] = max_trans_address[1]
				max_trans_address[1] = max_trans_address[0]
				max_trans_address[0] = out_id
			elif max_trans_value[0] >= int(value) and max_trans_value[1] < int(value):
				max_trans_value[2] = max_trans_value[1]
				max_trans_value[1] = int(value)

				max_trans_address[2] = max_trans_address[1]
				max_trans_address[1] = out_id
			elif max_trans_value[2] < int(value):
				max_trans_value[2] = int(value)
				max_trans_address[2] = out_id
	max_out = ""
	max_val = 0
	for key,val in addr_dict.iteritems():
		if max_val < val:
			max_val = val
			max_out = key

	f.close()

	fbi_addr = ""
	silk_rd_addr = ["","",""]
	with open("userMap.txt") as fi:
		for line in fi:
			addr_id,addr_list = line.split(" ",1)
			if addr_id == max_out:
				fbi_addr = addr_list.split(" ")[0]
			for i in range(0,3):
				if addr_id == max_trans_address[i]:
					silk_rd_addr[i] = addr_list.split(" ")[0]
	fi.close()
	#print "FBI user ID: " + max_out 
	print "FBI bitcoin address:" + fbi_addr
	print "Total bit coin seized:" + str(max_val)
	print "-------------------------------------"
	print "Silk road user bitcoin address and transactions:"
	for j in range(0,3):
		print "Address: " + silk_rd_addr[j] 
		print " with value in 1 transactions: " + str(max_trans_value[j])
	return

main()

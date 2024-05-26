filepath = "data.sql"
file = open(filepath, "w")

num_addresses = 20
num_telephones = 30
num_emails = 35
num_messages = 15
num_history = 13
num_contacts = 15


# ----- Address -----
    # state, smallint
    # contact_id, bigint
    # id, bigint
    # address, varchar(255)
for i in range(1,num_addresses+1):
    state = str(i%3)
    contact_id = str(i%num_contacts)
    id = str(i)
    address = "'" + str(i).zfill(3) + " Name" + str(i) + " Street'"
    file.write(f"insert into address values ({state}, {contact_id}, {id}, {address});\n")
file.write("\n")


# ----- Telephone -----
    # state, smallint
    # contact_id, bigint
    # id, bigint
    # telephone, varchar(255)
for i in range(1,num_telephones+1):
    state = str(i%3)
    contact_id = str(i%num_contacts)
    id = str(i)
    telephone = "'123-456-78" + str(i).zfill(2) + "'"
    file.write(f"insert into telephone values ({state}, {contact_id}, {id}, {telephone});\n")
file.write("\n")


# ----- Email -----
    # state, smallint
    # contact_id, bigint
    # id, bigint
    # email, varchar(255)
for i in range(1,num_emails+1):
    state = str(i%3)
    contact_id = str(i%num_contacts)
    id = str(i)
    email = "'email" + str(i) + "@example.com'"
    file.write(f"insert into email values ({state}, {contact_id}, {id}, {email});\n")
file.write("\n")


# ----- Message -----
    # channel, smallint
    # current_state, smallint
    # priority, smallint
    # date, timestamp(6)
    # id, bigint
    # address, varchar(255)
    # body, text
    # email, varchar(255)
    # subject, varchar(255)
    # telephone, varchar(255)
for i in range(1,num_messages+1):
    channel = str(i%3)
    current_state = str(i%3)
    priority = str(i%3)
    date = "'2024-04-01 00:00:" + str((2*i+3)%60).zfill(2) + ".123456'"
    id = str(i)
    address = "'" + str(i).zfill(3) + " Name" + str(i) + " Street'"
    body = "'Body" + str(i) + "'"
    email = "'email" + str(i) + "@example.com'"
    subject = "'Subject" + str(i) + "'"
    telephone = "'123-456-78" + str(i).zfill(2) + "'"
    file.write(f"insert into message values ({channel}, {current_state}, {priority}, {date}, {id}, {address}, {body}, {email}, {subject}, {telephone});\n")
file.write("\n")


# ----- History -----
    # state, smallint
    # date, timestamp(6)
    # id, bigint
    # message_id, bigint
    # comment, varchar(255)
for i in range(1,num_history+1):
    state = str(i%5)
    date = "'2024-04-01 00:03:" + str((2*i+3)%60).zfill(2) + ".123456'"
    id = str(i)
    message_id = str((7*i+1)%num_messages)
    comment = "'Comment" + str(i) + "'"
    file.write(f"insert into history values ({state}, {date}, {id}, {message_id}, {comment});\n")
file.write("\n")


# ----- Contact -----
    # category, smallint
    # id, bigint
    # name, varchar(255)
    # ssncode, varchar(255)
    # surname, varchar(255)
for i in range(1,num_contacts+1):
    category = str(i%3)
    id = str(i)
    name = "'Name" + str(i) + "'"
    ssncode = "'000-" + str(i).zfill(2) + "-" + str((7*i)%17).zfill(4) + "'"
    surname = "'Surname" + str(i) + "'"

    file.write(f"insert into contact values ({category}, {id}, {name}, {ssncode}, {surname});\n")

package main

import (
	"log"

	"github.com/amtc131/ampq-spring-batch/go-amqp/data"
	amqp "github.com/rabbitmq/amqp091-go"
)

func failOnError(err error, msg string) {
	if err != nil {
		log.Panicf("%s: %s", msg, err)
	}
}

func main() {
	conn, err := amqp.Dial("amqp://guest:guest@localhost:5672/")
	failOnError(err, "Failed to connect to RabbitMQ")

	defer conn.Close()

	ch, err := conn.Channel()
	failOnError(err, "Failed to open a channel")

	defer ch.Close()

	q, err := ch.QueueDeclare(
		"myqueue", //name
		true,      //durable
		false,     // delete when unused
		false,     // exclusive
		false,     // no-wait
		nil,       // arguments
	)

	failOnError(err, "Failed to declare a queue")

	msgs, err := ch.Consume(
		q.Name, // queue
		"",     // consummer
		true,   // auto-ack
		false,  // exclusive
		false,  // no-local
		false,  // no-wait
		nil,    // args
	)

	failOnError(err, "Failed to register a consumer")

	var forever chan struct{}
	var consumers data.Consumer
	go func() {
		for d := range msgs {
			data.Unmarshal(d.Body, &consumers)
			log.Printf("Recived a message: %#v", consumers)
		}
	}()

	log.Printf(" [*] Waiting for message. to exit press CTRL+C")

	<-forever

}

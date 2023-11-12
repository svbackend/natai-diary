import {Elements, PaymentElement, useStripe} from '@stripe/react-stripe-js';
import {loadStripe, StripeElementsOptions} from '@stripe/stripe-js';
import {cn} from "../../../utils/cn";
import React from "react";

type StripePaymentFormProps = {
    stripePublicKey: string,
    ephemeralKey: string,
    paymentIntentSecret: string,
    customerId: string,
}

export const StripePaymentForm = (props: StripePaymentFormProps) => {
    const stripePromise = loadStripe(props.stripePublicKey);

    const options: StripeElementsOptions = React.useMemo(() => {
        return {
            clientSecret: props.paymentIntentSecret,
            appearance: {
                theme: "night",
                labels: "floating",
            }
        };
    }, [props.paymentIntentSecret]);

    return (
        <Elements stripe={stripePromise} options={options}>
            <CheckoutForm/>
        </Elements>
    )
}

const CheckoutForm = () => {
    const stripe = useStripe();
    const elements = stripe?.elements({
        appearance: {
            theme: "night",
            labels: "floating",
        }
    })

    const buy = (e: any) => {
        e.preventDefault()

        console.log("buy", !!stripe, !!elements)

        if (!stripe || !elements) {
            // Stripe.js hasn't yet loaded.
            // Make sure to disable form submission until Stripe.js has loaded.
            return;
        }

        const currentSiteUrl = window.location.href;
        console.log("currentSiteUrl", currentSiteUrl)
        const result = stripe.confirmPayment({
            elements: elements,
            confirmParams: {
                return_url: currentSiteUrl + '/feature/success',
            },
        });

        result.then(res => {
            if (res.error) {
                console.log("res.error", res.error)
            }
        })
    };

    return (
        <form className={cn("max-w-2xl mx-auto")} onSubmit={buy}>
            <PaymentElement/>
            {stripe && (
                <div className={"mt-4"}>
                    <button
                        role={"button"}
                        className="p-6 text-white bg-brand hover:bg-brand/80 focus:ring-4 focus:outline-none focus:ring-indigo-900 font-bold rounded-full py-4">
                        Buy now
                    </button>
                </div>
            )}
        </form>
    )
};
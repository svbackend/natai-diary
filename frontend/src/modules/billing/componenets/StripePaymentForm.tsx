import {Elements, PaymentElement, useStripe} from '@stripe/react-stripe-js';
import {loadStripe, StripeElementsOptions} from '@stripe/stripe-js';
import {STRIPE_PUBLIC_KEY} from "../../../utils/env";
import PrimaryButton from "../../common/components/PrimaryButton";
import {cn} from "../../../utils/cn";

type StripePaymentFormProps = {
    ephemeralKey: string,
    paymentIntentSecret: string,
    customerId: string,
}

export const StripePaymentForm = (props: StripePaymentFormProps) => {
    // todo load stripe promise and render payment form using data from result
    console.log("STRIPE PUBLIC KEY", STRIPE_PUBLIC_KEY)

    if (!STRIPE_PUBLIC_KEY) {
        return <div>STRIPE_PUBLIC_KEY not set</div>
    }

    const stripePromise = loadStripe(STRIPE_PUBLIC_KEY);
    const options: StripeElementsOptions = {
        clientSecret: props.paymentIntentSecret,
        appearance: {
            theme: "night",
            labels: "floating",
        }
    };
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
    });

    const handleSubmit = async () => {
        if (!stripe || !elements) {
            // Stripe.js hasn't yet loaded.
            // Make sure to disable form submission until Stripe.js has loaded.
            return;
        }

        const currentSiteUrl = window.location.href;
        console.log("currentSiteUrl", currentSiteUrl)
        const result = await stripe.confirmPayment({
            //`Elements` instance that was used to create the Payment Element
            elements,
            confirmParams: {
                return_url: currentSiteUrl + '/feature/success',
            },
        });


        if (result.error) {
            // Show error to your customer (for example, payment details incomplete)
            console.log(result.error.message);
        } else {
            // Your customer will be redirected to your `return_url`. For some payment
            // methods like iDEAL, your customer will be redirected to an intermediate
            // site first to authorize the payment, then redirected to the `return_url`.
        }
    };

    return (
        <form className={cn("max-w-2xl mx-auto")}>
            <PaymentElement/>
            {stripe && (
                <PrimaryButton onClick={handleSubmit}>Buy</PrimaryButton>
            )}
        </form>
    )
};
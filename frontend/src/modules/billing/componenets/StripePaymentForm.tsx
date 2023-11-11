import {usePostLinksBuy} from "../../../api/apiComponents";
import AppSpinner from "../../common/components/AppSpinner";
import {AlertApiError} from "../../common/components/alert";

type StripePaymentFormProps = {
    stripeKey: string,
    ephemeralKey: string,
    paymentIntentSecret: string,
    customerId: string,
}

export const StripePaymentForm = (props: StripePaymentFormProps) => {
    // todo load stripe promise and render payment form using data from result

    return (
        <>
            {props.ephemeralKey} / {props.paymentIntentSecret} / {props.customerId}
        </>
    )
}
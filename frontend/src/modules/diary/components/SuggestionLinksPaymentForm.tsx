import {usePostLinksBuy} from "../../../api/apiComponents";
import AppSpinner from "../../common/components/AppSpinner";
import {AlertApiError} from "../../common/components/alert";
import React from "react";
import {StripePaymentForm} from "../../billing/componenets/StripePaymentForm";

export const SuggestionLinksPaymentForm = () => {
    const {data: result, isLoading, error} = usePostLinksBuy({})

    if (isLoading) {
        return <AppSpinner/>
    }

    if (error || !result) {
        return <AlertApiError error={error}/>
    }

    return (
        <StripePaymentForm
            stripeKey={""}
            ephemeralKey={result.ephemeralKey}
            paymentIntentSecret={result.paymentIntentSecret}
            customerI={result.customerId}
        />
    )
}
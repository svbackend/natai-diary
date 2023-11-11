import {usePostLinksBuy} from "../../../api/apiComponents";
import AppSpinner from "../../common/components/AppSpinner";
import {AlertApiError} from "../../common/components/alert";
import React, {useEffect} from "react";
import {StripePaymentForm} from "../../billing/componenets/StripePaymentForm";

export const SuggestionLinksPaymentForm = () => {
    const {
        mutate: buySuggestionLinks,
        data: result,
        isLoading,
        error
    } = usePostLinksBuy({})

    useEffect(() => {
        buySuggestionLinks({})
    }, [buySuggestionLinks])

    if (isLoading) {
        return <AppSpinner/>
    }

    if (error || !result) {
        return <AlertApiError error={error}/>
    }

    return (
        <StripePaymentForm
            ephemeralKey={result.ephemeralKey}
            paymentIntentSecret={result.paymentIntentSecret}
            customerId={result.customerId}
        />
    )
}
import {usePostLinksBuy} from "../../../api/apiComponents";
import {LoadingState} from "../../common/components/AppSpinner";
import {AlertApiError} from "../../common/components/alert";
import React, {useEffect} from "react";
import {StripePaymentForm} from "../../billing/componenets/StripePaymentForm";
import {STRIPE_PUBLIC_KEY} from "../../../utils/env";

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
        return <LoadingState/>
    }

    if (error || !result) {
        return <AlertApiError error={error}/>
    }

    if (!STRIPE_PUBLIC_KEY) {
        return <div>STRIPE_PUBLIC_KEY not set</div>
    }

    return (
        <StripePaymentForm
            stripePublicKey={STRIPE_PUBLIC_KEY}
            ephemeralKey={result.ephemeralKey}
            paymentIntentSecret={result.paymentIntentSecret}
            customerId={result.customerId}
        />
    )
}
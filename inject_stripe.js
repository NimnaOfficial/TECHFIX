const fs = require('fs');
let code = fs.readFileSync('cloudflare-backend/worker.js', 'utf8');

const marker = '      // ==========================================\n      // 8. NOTIFICATIONS';

const stripeCode =       // ==========================================
      // STRIPE PAYMENT INTENT
      // ==========================================
      if (path === "/api/create-payment-intent" && request.method === "POST") {
        const user = await authenticate(request, env);
        if (!user) return json({ success: false, message: "Unauthorized" }, 401);

        const body = await request.json();
        const { repairId, amount } = body;

        if (!repairId) {
          return json({ success: false, message: "repairId is required" }, 400);
        }
        if (!amount || typeof amount !== "number" || amount <= 0) {
          return json({ success: false, message: "Valid amount (in cents) is required" }, 400);
        }

        const stripeSecretKey = env.STRIPE_SECRET_KEY;
        if (!stripeSecretKey) {
          console.error("STRIPE_SECRET_KEY is not set in environment variables");
          return json({ success: false, message: "Payment service configuration error" }, 500);
        }

        try {
          const formData = new URLSearchParams();
          formData.append("amount", amount.toString());
          formData.append("currency", "usd");
          formData.append("metadata[repair_id]", repairId);
          formData.append("metadata[user_id]", user.id);

          const stripeResponse = await fetch("https://api.stripe.com/v1/payment_intents", {
            method: "POST",
            headers: {
              "Authorization": \Bearer \\,
              "Content-Type": "application/x-www-form-urlencoded",
            },
            body: formData.toString(),
          });

          const stripeData = await stripeResponse.json();

          if (!stripeResponse.ok) {
            console.error("Stripe API Error:", stripeData);
            return json({
              success: false,
              message: stripeData.error?.message || "Payment service error"
            }, 400);
          }

          return json({
            success: true,
            clientSecret: stripeData.client_secret,
            paymentId: stripeData.id,
            status: stripeData.status
          });

        } catch (error) {
          console.error("Stripe request failed:", error);
          return json({
            success: false,
            message: "Payment service unavailable"
          }, 500);
        }
      }
;

if (code.includes(marker)) {
    code = code.replace(marker, stripeCode + '\n' + marker);
    fs.writeFileSync('cloudflare-backend/worker.js', code);
    console.log("Stripe endpoint injected successfully!");
} else {
    console.log("Marker not found!");
}

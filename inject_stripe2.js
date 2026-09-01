const fs = require('fs');
let code = fs.readFileSync('cloudflare-backend/worker.js', 'utf8');

const marker = '      // ==========================================\n      // 8. NOTIFICATIONS';

const stripeCode = '      // ==========================================\n' +
'      // STRIPE PAYMENT INTENT\n' +
'      // ==========================================\n' +
'      if (path === "/api/create-payment-intent" && request.method === "POST") {\n' +
'        const user = await authenticate(request, env);\n' +
'        if (!user) return json({ success: false, message: "Unauthorized" }, 401);\n' +
'\n' +
'        const body = await request.json();\n' +
'        const { repairId, amount } = body;\n' +
'\n' +
'        if (!repairId) {\n' +
'          return json({ success: false, message: "repairId is required" }, 400);\n' +
'        }\n' +
'        if (!amount || typeof amount !== "number" || amount <= 0) {\n' +
'          return json({ success: false, message: "Valid amount (in cents) is required" }, 400);\n' +
'        }\n' +
'\n' +
'        const stripeSecretKey = env.STRIPE_SECRET_KEY;\n' +
'        if (!stripeSecretKey) {\n' +
'          console.error("STRIPE_SECRET_KEY is not set in environment variables");\n' +
'          return json({ success: false, message: "Payment service configuration error" }, 500);\n' +
'        }\n' +
'\n' +
'        try {\n' +
'          const formData = new URLSearchParams();\n' +
'          formData.append("amount", amount.toString());\n' +
'          formData.append("currency", "usd");\n' +
'          formData.append("metadata[repair_id]", repairId);\n' +
'          formData.append("metadata[user_id]", user.id);\n' +
'\n' +
'          const stripeResponse = await fetch("https://api.stripe.com/v1/payment_intents", {\n' +
'            method: "POST",\n' +
'            headers: {\n' +
'              "Authorization": "Bearer " + stripeSecretKey,\n' +
'              "Content-Type": "application/x-www-form-urlencoded",\n' +
'            },\n' +
'            body: formData.toString(),\n' +
'          });\n' +
'\n' +
'          const stripeData = await stripeResponse.json();\n' +
'\n' +
'          if (!stripeResponse.ok) {\n' +
'            console.error("Stripe API Error:", stripeData);\n' +
'            return json({\n' +
'              success: false,\n' +
'              message: stripeData.error?.message || "Payment service error"\n' +
'            }, 400);\n' +
'          }\n' +
'\n' +
'          return json({\n' +
'            success: true,\n' +
'            clientSecret: stripeData.client_secret,\n' +
'            paymentId: stripeData.id,\n' +
'            status: stripeData.status\n' +
'          });\n' +
'\n' +
'        } catch (error) {\n' +
'          console.error("Stripe request failed:", error);\n' +
'          return json({\n' +
'            success: false,\n' +
'            message: "Payment service unavailable"\n' +
'          }, 500);\n' +
'        }\n' +
'      }\n';

if (code.includes(marker)) {
    code = code.replace(marker, stripeCode + '\n' + marker);
    fs.writeFileSync('cloudflare-backend/worker.js', code);
    console.log("Stripe endpoint injected successfully!");
} else {
    console.log("Marker not found!");
}

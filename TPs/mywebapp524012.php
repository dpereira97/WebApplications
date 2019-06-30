<html>
    <form action='mywebapp.php' method='get'>
        <p> Disease: <input type='text' name='disease' /> </p>
        <p><input type='submit' /> </p>
    </form>

<p>Abstracts about the disease <?php echo htmlspecialchars($_GET['disease']); ?>:</p>

<?php

...
$c=array_combine($links,$titles);

echo '<div vocab="http://schema.org/" typeof="ScholarlyArticle" resource="#article">';

foreach ($c as $key => $value) {
  echo '<span property="name">';
  echo '<a href="' . $key . '">' . $value . '</a></br>'; 
  echo '</span>';
}

echo '</div>';
...

?>
</html>

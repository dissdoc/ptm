class AddReviewsToPhoto < ActiveRecord::Migration
  def change
    add_column :photos, :review, :integer

  end
end
